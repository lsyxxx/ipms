package com.abt.sys.service.impl;

import com.abt.common.util.ValidateUtil;
import com.abt.sys.config.EnumLibConfig;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.EnumLibService;
import com.abt.testing.entity.EnumLib;
import com.abt.testing.repository.EnumLibRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class EnumLibServiceImpl implements EnumLibService {

    private final EnumLibRepository enumLibRepository;
    private final EnumLibConfig enumLibConfig;

    public EnumLibServiceImpl(EnumLibRepository enumLibRepository, EnumLibConfig enumLibConfig) {
        this.enumLibRepository = enumLibRepository;
        this.enumLibConfig = enumLibConfig;
    }

    @Override
    public void saveEnumLib(EnumLib enumLib) {
        validateEnumLib(enumLib);
        enumLibRepository.save(enumLib);
    }

    public void validateEnumLib(EnumLib enumLib) {
        //FID type内唯一
        String id = enumLib.getId();
        final List<EnumLib> list = enumLibRepository.findAllByFtypeidOrderByFid(enumLib.getFtypeid());
        list.stream().filter(i -> !i.getId().equals(enumLib.getFid()) && i.getFid().equals(enumLib.getFid()))
                .findAny().ifPresent(i -> { throw new BusinessException("fid(" + enumLib.getFid() + ")已存在，不能重复!");});
    }

    @Override
    public void deleteEnumLib(String id) {
        ValidateUtil.ensurePropertyNotnull(id, "EnumLib:id");
        enumLibRepository.deleteById(id);
    }

    @Override
    public List<EnumLib> findEnumLibsBy(String type) {
        ValidateUtil.ensurePropertyNotnull(type, "EnumLib:ftypeid");
        return enumLibRepository.findAllByFtypeidOrderByFid(type);
    }


    @Override
    public Map<String, List<EnumLib>> findAllEnum() {
        return enumLibConfig.getAllEnum();
    }


}
