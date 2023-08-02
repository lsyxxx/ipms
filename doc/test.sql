#查询所有function及关联的role
select sf.id, sf.name, sf.remark, sf.url, r.role_code, r.role_name, r.role_des
from sys_function sf
         left join (
    select srf.func_id as fid, srf.role_id as role_id, sr.name as role_name, sr.code as role_code, sr.description as role_des
    from sys_role_func srf
             left join sys_role sr on srf.role_id = sr.id
) as r on sf.id = r.fid;