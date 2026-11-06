# 仅升级本地 ZJData：旧 JSON 姓名转纯文本，以“、”连接；不修改审计、费用或车辆状态。
# JSON 解析由 PowerShell 完成，数据库端仅使用 SQL Server 2014 支持的语法。
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path -Parent $PSScriptRoot
$taskConfig = [IO.File]::ReadAllText((Join-Path $taskRoot 'admin/src/main/resources/application-dev.yml'))
$taskPassword = [regex]::Match($taskConfig, '(?m)^    password: "([^"]+)"')
if (-not $taskPassword.Success) { throw '未找到本地数据库凭据' }
$builder = [System.Data.SqlClient.SqlConnectionStringBuilder]::new()
$builder['Data Source'] = 'localhost,1433'
$builder['Initial Catalog'] = 'ZJData'
$builder['User ID'] = 'sa'
$builder['Password'] = $taskPassword.Groups[1].Value
$builder['TrustServerCertificate'] = $true
$connection = [System.Data.SqlClient.SqlConnection]::new($builder.ConnectionString)
$connection.Open()
$transaction = $connection.BeginTransaction()
try {
    $command = $connection.CreateCommand()
    $command.Transaction = $transaction
    $command.CommandText = "SELECT COUNT(*) FROM sys.columns WHERE object_id=OBJECT_ID(N'dbo.oa_field_vehicle_use') AND name IN ('drivers_json','peers_json')"
    $legacyColumns = [int]$command.ExecuteScalar()
    if ($legacyColumns -eq 0) {
        $command.CommandText = "SELECT COUNT(*) FROM sys.columns WHERE object_id=OBJECT_ID(N'dbo.oa_field_vehicle_use') AND name IN ('drivers','peers')"
        if ([int]$command.ExecuteScalar() -ne 2) { throw '人员字段结构不匹配' }
        $transaction.Commit()
        Write-Output '人员字段已是纯文本，无需重复升级。'
    } else {
        if ($legacyColumns -ne 2) { throw '旧人员字段结构不完整' }
        $command.CommandText = "SELECT COUNT(*) FROM sys.columns WHERE object_id=OBJECT_ID(N'dbo.oa_field_vehicle_use') AND name IN ('drivers','peers')"
        $newColumns = [int]$command.ExecuteScalar()
        if ($newColumns -notin @(0,2)) { throw '新人员字段结构不完整' }
        $command.CommandText = if ($newColumns -eq 2) { 'SELECT id, drivers_json, peers_json, drivers, peers FROM dbo.oa_field_vehicle_use WITH (TABLOCKX,HOLDLOCK)' } else { 'SELECT id, drivers_json, peers_json FROM dbo.oa_field_vehicle_use WITH (TABLOCKX,HOLDLOCK)' }
        $table = [System.Data.DataTable]::new()
        $reader = $command.ExecuteReader()
        $table.Load($reader)
        $reader.Close()
        $command.CommandText = "IF OBJECT_ID(N'dbo.DF_ofvu_drivers',N'D') IS NOT NULL ALTER TABLE dbo.oa_field_vehicle_use DROP CONSTRAINT DF_ofvu_drivers; IF OBJECT_ID(N'dbo.DF_ofvu_peers',N'D') IS NOT NULL ALTER TABLE dbo.oa_field_vehicle_use DROP CONSTRAINT DF_ofvu_peers; ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN drivers_json NVARCHAR(MAX) NULL; ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN peers_json NVARCHAR(MAX) NULL;"
        $null = $command.ExecuteNonQuery()
        foreach ($row in $table.Rows) {
            $values = @()
            foreach ($column in @('drivers_json','peers_json')) {
                $targetColumn = if ($column -eq 'drivers_json') { 'drivers' } else { 'peers' }
                if ($newColumns -eq 2 -and $row[$targetColumn] -isnot [DBNull]) { $values += [string]$row[$targetColumn]; continue }
                if ($row[$column] -is [DBNull]) { $values += [DBNull]::Value; continue }
                $people = @(([string]$row[$column]) | ConvertFrom-Json)
                foreach ($person in $people) {
                    if ($null -eq $person -or $person.name -isnot [string]) { throw '旧姓名JSON格式异常，取消迁移' }
                }
                $values += (($people | ForEach-Object { $_.name }) -join '、')
            }
            $command.Parameters.Clear()
            $command.CommandText = if ($newColumns -eq 2) { 'UPDATE dbo.oa_field_vehicle_use SET drivers=@drivers, peers=@peers WHERE id=@id' } else { 'UPDATE dbo.oa_field_vehicle_use SET drivers_json=@drivers, peers_json=@peers WHERE id=@id' }
            $null = $command.Parameters.Add('@drivers',[System.Data.SqlDbType]::NVarChar,-1)
            $command.Parameters['@drivers'].Value = $values[0]
            $null = $command.Parameters.Add('@peers',[System.Data.SqlDbType]::NVarChar,-1)
            $command.Parameters['@peers'].Value = $values[1]
            $null = $command.Parameters.Add('@id',[System.Data.SqlDbType]::VarChar,50)
            $command.Parameters['@id'].Value = $row['id']
            $null = $command.ExecuteNonQuery()
        }
        $command.Parameters.Clear()
        $command.CommandText = if ($newColumns -eq 2) { 'ALTER TABLE dbo.oa_field_vehicle_use DROP COLUMN drivers_json, peers_json' } else { "EXEC sys.sp_rename N'dbo.oa_field_vehicle_use.drivers_json', N'drivers', N'COLUMN'; EXEC sys.sp_rename N'dbo.oa_field_vehicle_use.peers_json', N'peers', N'COLUMN';" }
        $null = $command.ExecuteNonQuery()
        $transaction.Commit()
        Write-Output "已迁移 $($table.Rows.Count) 条记录，司机及同行人改为纯文本。"
    }
} catch {
    $transaction.Rollback()
    throw
} finally {
    $connection.Dispose()
}
