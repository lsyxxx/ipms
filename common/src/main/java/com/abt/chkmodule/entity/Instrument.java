package com.abt.chkmodule.entity;

import com.abt.common.AuditInfo;
import com.abt.chkmodule.converter.ListStringConverter;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 实验仪器设备
 */
@Getter
@Setter
@Entity
@Table(name = "check_instrument")
public class Instrument extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 128)
    private String id;

    /**
     * 排序序号
     */
    @Column(name="sort_no")
    private Integer sortNo;

    /**
     * 设备名称
     */
    @Column(name="name_", nullable = false)
    private String name;

    /**
     * 档案盒号
     */
    @Column(name="box_no", length = 16)
    private String boxNo;

    /**
     * 设备分类，EnumlibshebeiType
     */
    @NotNull
    @Column(name = "type_", nullable = false, length = 2)
    private String type;

    /**
     * 设备状态
     * todo: 使用字典表翻译
     * 1. 使用中
     */
    @Size(max = 2)
    @NotNull
    @Column(name = "status_", nullable = false, length = 2)
    private String status = STATUS_OK;

    public static final String STATUS_OK = "1";


    /**
     * 设备编号
     * TODO: 根据其他数据自动生成
     */
    @Size(max = 32)
    @Column(name = "code_", length = 32, nullable = false)
    private String code;


    /**
     * 设备规格
     */
    @Size(max = 32)
    @NotNull
    @Column(name = "spec_", nullable = false, length = 32)
    private String specification;

    /**
     * 设备数量
     */
    @Column(name = "num_")
    private Integer num;

    /**
     * 制造厂家
     */
    @Column(name = "manufacturer")
    private String manufacturer;

    /**
     * 出厂编号
     */
    @Column(name = "factory_code")
    private String factoryCode;

    /**
     * 测量范围
     */
    @Column(name = "measure_range")
    private String measureRange;

    /**
     * 示值误差或准确度等级
     */
    @Column(name = "accuracy")
    private String accuracy;

    /**
     * 资产来源，允许用户自由输入
     */
    @Column(name = "source")
    private String source;

    /**
     * 购入时间
     */
    @Column(name = "buy_time")
    private String buyTime;

    /**
     * 使用部门名称
     */
    @Column(name = "use_dept", length = 128)
    private String useDept;

    /**
     * 负责人userid
     */
    @Column(name = "user_id", length = 128)
    private String userid;

    /**
     * 负责人姓名
     */
    @Column(name="user_name", length = 32)
    private String username;

    /**
     * 安装地址
     */
    @Column(name = "setup_addr")
    private String setupAddress;

    /**
     * 供应商
     */
    @Column(name = "supplier")
    private String supplier;

    /**
     * 供应商联系人
     */
    @Column(name = "supplier_user", length = 32)
    private String supplierUser;

    /**
     * 供应商联系人电话
     */
    @Column(name = "supplier_tel", length = 128)
    private String supplierTel;

    /**
     * 备注
     */
    @Column(name = "note_", length = 512)
    private String note;

    /**
     * 附件地址
     */
    @Lob
    @Column(name = "file_path")
    @Convert(converter = SystemFileListConverter.class)
    private List<SystemFile> filePath;

    /**
     * 设备图片
     */
    @Lob
    @Column(name="images")
    @Convert(converter = ListStringConverter.class)
    private List<String> imageUrls;

    /**
     * 价格
     */
    @Column(name = "price", columnDefinition = "decimal(11,2)")
    private Double price;

}