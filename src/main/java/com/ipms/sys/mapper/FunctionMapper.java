package com.ipms.sys.mapper;

import com.ipms.sys.model.entity.Function;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能权限
 */
@Mapper
public interface FunctionMapper extends BaseMapper<Function, Function> {

    @Override
    @Select("select * from sys_function where id = #{id}")
    Function findById(@Param("id")Long id);


    /**
     * 查询所有未删除的
     * @return
     */
    @Select("select * from sys_function where delete_flag='0'")
    List<Function> findVisible();

    long count();

    /**
     * 插入一条数据，插入成功后返回id
     */
    @Insert("insert into sys_function (path, name, pid, url, remark, component, state, sort, enabled, type, push_btn, icon) values (#{f.path}, #{f.name}, #{f.pid}, #{f.url}, #{f.remark}, #{f.component}, #{f.state}, #{f.sort}, #{f.enabled}, #{f.type}, #{f.pushBtn}, #{f.icon})")
    long insert(@Param("f") Function f);

    @Update("update sys_function set delete_flag='1' where id = #{id}")
    void softDelete(@Param("id") Long id);

    @Update("update sys_function set enabled = #{function.enabled} where id = #{function.id}")
    void updateEnabled(@Param("function") Function function);

}
