package com.abt.wxapp.checkmodule.repository;

import com.abt.wxapp.checkmodule.entity.CheckComponent;
import com.abt.wxapp.checkmodule.entity.DynamicForm;
import com.abt.wxapp.checkmodule.entity.options.*;
import com.abt.wxapp.checkmodule.model.CheckComponentType;
import com.abt.wxapp.checkmodule.model.PayType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DynamicFormRepositoryTest {

    @Autowired
    private DynamicFormRepository dynamicFormRepository;

    /**
     * 参考前端 traceElementSchema，字段以 Java 模型为准（type 为枚举、options 带 kind、上传为 ImageUploadOptions/FileUploadOptions 等）。
     */
    static DynamicForm newTraceElementDynamicForm() {
        DynamicForm form = new DynamicForm();
        form.setCheckModuleId("traceElementSchema");
        form.setCheckModuleName("微量元素分析");
        form.setTitle("微量元素分析");
        form.setDescription(truncateOrderNotice());

        List<CheckComponent> components = new ArrayList<>();

        // 1. 样品数量 number
        CheckComponent sampleCount = new CheckComponent();
        sampleCount.setId("sampleCount");
        sampleCount.setLabel("样品数量");
        sampleCount.setType(CheckComponentType.number);
        sampleCount.setRequired(true);
        NumberInputOptions numOpts = new NumberInputOptions();
        numOpts.setSuffix("个");
        sampleCount.setOptions(numOpts);
        components.add(sampleCount);

        // 2. 制备 radio + payType/price
        CheckComponent prepared = new CheckComponent();
        prepared.setId("prepared");
        prepared.setLabel("用户送样样品是否需制备");
        prepared.setType(CheckComponentType.radio);
        prepared.setRequired(true);
        prepared.setPayType(PayType.sample);
        prepared.setPrice(new BigDecimal("2000"));
        RadioOptions radioOpts = new RadioOptions();
        radioOpts.setItems(List.of(
                new OptionItem("1", "需制备", null),
                new OptionItem("2", "无需制备", null)
        ));
        prepared.setOptions(radioOpts);
        prepared.setTips("非岩石粉末需要制样。如是特殊样品，请联系技术老师确认是否需要制样");
        components.add(prepared);

        // 3. 元素 checkbox
        CheckComponent elements = new CheckComponent();
        elements.setId("result_param1");
        elements.setLabel("检测结果是否出具锰(Mn) 铭(Bi) 砷(As) 钛(Ti) 锢(In) 钨(W) 元素数据(默认无此6个元素数据)");
        elements.setType(CheckComponentType.checkbox);
        elements.setRequired(true);
        elements.setPayType(PayType.sample);
        elements.setPrice(new BigDecimal("600"));
        CheckboxOptions cbOpts = new CheckboxOptions();
        cbOpts.setItems(List.of(
                new OptionItem("1", "锰(Mn)", null),
                new OptionItem("2", "铭(Bi)", null),
                new OptionItem("3", "砷(As)", null),
                new OptionItem("4", "钛(Ti)", null),
                new OptionItem("5", "锢(In)", null),
                new OptionItem("6", "钨(W)", null),
                new OptionItem("7", "默认", null)
        ));
        elements.setOptions(cbOpts);
        elements.setTips("锰(Mn) 铭(Bi) 砷(As) 钛(Ti) 锢(In) 钨(W) 因元素特性，检测结果数据仅供参考");
        components.add(elements);

        // 4. 检测要求 textarea
        CheckComponent remark = new CheckComponent();
        remark.setId("testRemark");
        remark.setLabel("检测要求");
        remark.setType(CheckComponentType.textarea);
        remark.setRequired(false);
        TextareaOptions taOpts = new TextareaOptions();
        taOpts.setPlaceholder("可填写对检测实验的具体要求，最多不可超过250字。也可直接联系技术老师.");
        taOpts.setMax(250);
        remark.setOptions(taOpts);
        components.add(remark);

        // 5. 上传照片 imageUpload
        CheckComponent imageUpload = new CheckComponent();
        imageUpload.setId("image-upload");
        imageUpload.setLabel("上传照片");
        imageUpload.setType(CheckComponentType.imageUpload);
        imageUpload.setRequired(false);
        ImageUploadOptions imgOpts = new ImageUploadOptions();
        imgOpts.setMax(6);
        imgOpts.setSizeLimit(10.0);
        imgOpts.setAccept(List.of("image/jpeg", "image/png", "image/jpg"));
        imageUpload.setOptions(imgOpts);
        imageUpload.setTips("最多上传6张照片");
        components.add(imageUpload);

        // 6. 上传附件 fileUpload
        CheckComponent fileUpload = new CheckComponent();
        fileUpload.setId("file-upload");
        fileUpload.setLabel("上传附件");
        fileUpload.setType(CheckComponentType.fileUpload);
        fileUpload.setRequired(false);
        FileUploadOptions fileOpts = new FileUploadOptions();
        fileOpts.setMax(3);
        fileOpts.setSizeLimit(10.0);
        fileOpts.setAccept(List.of("xlsx", "docx", "pdf"));
        fileUpload.setOptions(fileOpts);
        fileUpload.setTips("支持 xlsx/docx/pdf，从微信聊天记录中选取，仅能上传3个附件");
        components.add(fileUpload);

        form.setComponents(components);
        return form;
    }

    /**
     * {@link DynamicForm#getDescription()} 带 max=500，完整 orderNotice 过长时截断
     */
    private static String truncateOrderNotice() {
        String full = """
                1. 样品可以是岩石块样，块样需制备后成粉末后进行检测;
                2. 如客户提供粉末样品，样品粒径至少小于200目。至少10g粉末样品
                3. 样品必须具有代表性
                4. 报告中默认出39个元素数据。因锰(Mn) 铭(Bi) 砷(As) 钛(Ti) 锢(In) 钨(W) 因元素特性，检测报告中不体现这6个元素，若需这6个元素，请在检测项目中选择要出数据的元素，检测结果仅供参考。
                5. 有特殊要求，请提前与技术老师沟通联系。点击下方客服图标可联系技术老师""";
        String compact = full.replaceAll("\\s+", " ").trim();
        return compact.length() <= 500 ? compact : compact.substring(0, 500);
    }

    @Test
    void save_traceElementSchema() {
        DynamicForm form = newTraceElementDynamicForm();
        DynamicForm form2 = newTraceElementDynamicForm();
        assertNull(form.getId());

        DynamicForm saved = dynamicFormRepository.save(form);
        DynamicForm save2 = dynamicFormRepository.save(form2);
//        assertNotNull(saved.getId());
//        assertEquals("traceElementSchema", saved.getCheckModuleId());
//        assertEquals(6, saved.getComponents().size());
//
//        String id = Objects.requireNonNull(saved.getId());
//        DynamicForm loaded = dynamicFormRepository.findById(id).orElseThrow();
//        assertEquals(saved.getId(), loaded.getId());
//        assertEquals(6, loaded.getComponents().size());
//        assertEquals(CheckComponentType.number, loaded.getComponents().get(0).getType());
//        assertEquals("sampleCount", loaded.getComponents().get(0).getId());
//
        System.out.println("end....");
    }

    @Test
    void countByCheckModule() {
    }
}
