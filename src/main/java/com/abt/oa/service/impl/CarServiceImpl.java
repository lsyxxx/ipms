package com.abt.oa.service.impl;

import com.abt.oa.service.CarService;

/**
 *
 */
//@Service
public class CarServiceImpl implements CarService {

//    private final CarApplyRecordRepository carApplyRecordRepository;
//
//    public CarServiceImpl(CarApplyRecordRepository carApplyRecordRepository) {
//        this.carApplyRecordRepository = carApplyRecordRepository;
//    }
//
//    @Override
//    public List<CarApplyRecord> findBy(CarApplyRequestForm requestForm) {
//        //格式化date
//        LocalDateTime start = null;
//        LocalDateTime end = null;
//        if (StringUtils.isNotBlank(requestForm.getStartDate())) {
//            start = LocalDate.parse(requestForm.getStartDate()).atStartOfDay();
//        }
//        if (StringUtils.isNotBlank(requestForm.getEndDate())) {
//            end = LocalDate.parse(requestForm.getEndDate()).atStartOfDay();
//        }
//        final List<CarApplyRecord> list = carApplyRecordRepository.findByKeywordAndDate(requestForm.getQuery(), start, end);
//        if (list != null && !list.isEmpty()) {
//            list.forEach(i -> i.setCarNo(i.getCarInfo().getCarNo()));
//        }
//
//        return list;
//    }
//
//    @Override
//    public void writeExcel(List<CarApplyRecord> list) throws IOException {
//        CarApplyExcel.copyModel(CarApplyExcel.carApplyTemplatePath, CarApplyExcel.copyFilePath);
//        Map<String, Object> map = MapUtils.newHashMap();
//        map.put("keyword", "陕U");
//        map.put("startDate", "2024-06-06");
//        map.put("endDate", "2024-06-06");
//        CarApplyExcel.writeTemplate(CarApplyExcel.carApplyTemplatePath, CarApplyExcel.copyFilePath, true, list, map);
//        System.out.println("===== 写入excel完成 ====== ");
//    }
}
