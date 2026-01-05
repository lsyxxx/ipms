package com.abt.market.entity;

import com.abt.market.model.ImportSample;
import com.abt.sys.exception.BusinessException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;


/**
 * sltm_test临时表，用于临时保存导入数据
 */
@Getter
@Setter
@Entity
@Table(name = "stlm_test_temp")
@NoArgsConstructor
public class StlmTestTemp {
    @Id
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    private String id;

    @Size(max = 255)
    @Column(name = "check_module_id")
    private String checkModuleId;

    @Size(max = 100)
    @Column(name = "check_module_name", length = 100)
    private String checkModuleName;

    @Size(max = 50)
    @Column(name = "entrust_id", length = 50)
    private String entrustId;

    /**
     * 临时key
     */
    @Size(max = 255)
    @Column(name = "m_id")
    private String tempMid;

    @Column(name = "price_", precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 255)
    @NotNull
    @Column(name = "sample_no", nullable = false)
    private String sampleNo;

    @Size(max = 12)
    @Column(name = "sample_unit", length = 12)
    private String sampleUnit;

    @Size(max = 255)
    @Column(name = "old_sample_no")
    private String oldSampleNo;

    @Size(max = 255)
    @Column(name = "well_no")
    private String wellNo;

    /**
     * 导入时的排序
     */
    @Column(name="sort_no")
    private Integer sortNo;

    public static StlmTestTemp from(ImportSample sample, String tempMid) {
        if (StringUtils.isBlank(tempMid)) {
            throw new IllegalArgumentException("tempMid不能为空");
        }
        StlmTestTemp stlmTestTemp = new StlmTestTemp();
        stlmTestTemp.setId(UUID.randomUUID().toString());
        stlmTestTemp.setCheckModuleId(sample.getCheckModuleId());
        stlmTestTemp.setCheckModuleName(sample.getCheckModuleName());
        stlmTestTemp.setEntrustId(sample.getProjectNo());
        //是否可以转为BigDecimal
        if (StringUtils.isNotBlank(sample.getPrice())) {
            try {
                stlmTestTemp.setPrice(new BigDecimal(sample.getPrice()));
            } catch (NumberFormatException e) {
                throw new BusinessException(String.format("单价%s格式错误", sample.getPrice()));
            }
        }
        stlmTestTemp.setSampleNo(sample.getSampleNo());
        stlmTestTemp.setTempMid(tempMid);
        return  stlmTestTemp;
    }



}