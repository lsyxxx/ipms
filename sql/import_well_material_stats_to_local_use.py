import argparse
import json
import re
from datetime import datetime
from pathlib import Path

import openpyxl


TABLE_NAME = "dbo.well_material_stats_20251231"
SOURCE_FILE = r"C:\Users\Administrator\Desktop\2025年入井材料统计2025.12.31.xlsx"


def to_text(value):
    if value is None:
        return None
    if isinstance(value, datetime):
        return value.strftime("%Y-%m-%d")
    text = str(value).strip()
    return text or None


def to_int(value):
    if value in (None, ""):
        return None
    try:
        return int(value)
    except Exception:
        return None


def std_entrust_id(value):
    text = to_text(value)
    if not text:
        return None
    return re.sub(r"[A-Za-z]$", "", text)


def sql_literal(value):
    if value is None:
        return "NULL"
    if isinstance(value, int):
        return str(value)
    return "N'" + str(value).replace("'", "''") + "'"


def load_rows(workbook_path):
    wb = openpyxl.load_workbook(workbook_path, read_only=True, data_only=True)
    ws = wb["数据处理"]
    rows = []
    invalid_rows = []

    for row_idx, row in enumerate(ws.iter_rows(min_row=2, max_col=6, values_only=True), start=2):
        base = {
            "receive_time": to_text(row[0]),
            "entrust_id": to_text(row[1]),
            "sample_type": to_text(row[2]),
            "client_name": to_text(row[3]),
            "sample_num": to_int(row[4]),
            "finish_status": to_text(row[5]),
        }
        if not base["entrust_id"]:
            invalid_rows.append({
                "sheet": "数据处理",
                "row_index": row_idx,
                "missing_fields": ["entrust_id"],
                **base,
            })
            continue
        base["std_entrust_id"] = std_entrust_id(base["entrust_id"])
        rows.append(base)

    return rows, invalid_rows


def build_sql(rows, table_name):
    columns = [
        "receive_time",
        "entrust_id",
        "std_entrust_id",
        "sample_type",
        "client_name",
        "sample_num",
        "finish_status",
    ]
    sql = [
        "SET NOCOUNT ON;",
        "BEGIN TRANSACTION;",
        f"IF OBJECT_ID(N'{table_name}', N'U') IS NOT NULL DROP TABLE {table_name};",
        f"""
CREATE TABLE {table_name} (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    receive_time NVARCHAR(50) NULL,
    entrust_id NVARCHAR(100) NOT NULL,
    std_entrust_id NVARCHAR(100) NULL,
    sample_type NVARCHAR(255) NULL,
    client_name NVARCHAR(255) NULL,
    sample_num INT NULL,
    finish_status NVARCHAR(100) NULL
);
""".strip(),
    ]
    for row in rows:
        values = ", ".join(sql_literal(row[column]) for column in columns)
        sql.append(
            f"INSERT INTO {table_name} ({', '.join(columns)}) VALUES ({values});"
        )
    sql.extend(
        [
            "COMMIT TRANSACTION;",
            f"SELECT COUNT(1) AS imported_count FROM {table_name};",
        ]
    )
    return "\n".join(sql)


def main():
    parser = argparse.ArgumentParser(description="Generate SQL for importing well material stats Excel data.")
    parser.add_argument("--source", default=SOURCE_FILE, help="Excel source file path")
    parser.add_argument("--table", default=TABLE_NAME, help="Target table name")
    parser.add_argument("--output", default="sql/well_material_stats_20251231_import.sql", help="Output SQL file path")
    parser.add_argument("--issues-output", default="sql/well_material_stats_20251231_issues.json", help="Output JSON file for invalid rows")
    args = parser.parse_args()

    rows, invalid_rows = load_rows(args.source)
    issues_path = Path(args.issues_output)
    issues_path.write_text(json.dumps(invalid_rows, ensure_ascii=False, indent=2), encoding="utf-8")

    if invalid_rows:
        print(f"source={args.source}")
        print(f"table={args.table}")
        print(f"issues_output={issues_path.resolve()}")
        print(f"invalid_rows={len(invalid_rows)}")
        for item in invalid_rows[:20]:
            print(f"invalid_row={json.dumps(item, ensure_ascii=False)}")
        raise SystemExit(1)

    sql = build_sql(rows, args.table)
    output_path = Path(args.output)
    output_path.write_text(sql, encoding="utf-8")

    print(f"source={args.source}")
    print(f"table={args.table}")
    print(f"output={output_path.resolve()}")
    print(f"issues_output={issues_path.resolve()}")
    print(f"rows_to_import={len(rows)}")
    print("invalid_rows=0")


if __name__ == "__main__":
    main()
