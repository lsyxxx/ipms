package com.abt.sys.service.impl;

import com.abt.common.util.ValidateUtil;
import com.abt.sys.config.EnumLibConfig;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.EnumLibService;
import com.abt.testing.entity.EnumLib;
import com.abt.testing.repository.EnumLibRepository;
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
    public void updateEnumLib(EnumLib enumLib) {
        enumLibRepository.save(enumLib);
    }

    private String certTypeIdGenerator(int total) {
        return "ZJ" + (total + 1);
    }

    private String certTypeFidGenerator(int total) {
        return String.valueOf((total + 1));
    }

    @Override
    public EnumLib newCertEnumLib() {
        EnumLib enumLib = new EnumLib();
        enumLib.setFdesc("证件类型");
        enumLib.setFtypeid("EnumLibCertType");
        //ID
        final int total = enumLibRepository.countByFtypeid("EnumLibCertType");
        enumLib.setId(certTypeIdGenerator(total));
        //FID
        enumLib.setFid(certTypeFidGenerator(total));
        return enumLib;
    }

    @Override
    public void createEnumLib(EnumLib enumLib) {
        //validate
        //1. id
        final int countById = enumLibRepository.countById(enumLib.getId());
        if (countById > 0) {
            throw new BusinessException("ID重复(" + enumLib + ")");
        }
        //2. fid
        final List<EnumLib> list = enumLibRepository.findAllByFtypeidOrderByFid(enumLib.getFtypeid());
        list.stream().filter(i -> i.getFid().equals(enumLib.getFid())).findAny().ifPresent(i -> {
            throw new BusinessException("FID重复(" + enumLib.getFid() + ")!");
        });
        enumLibRepository.save(enumLib);
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
