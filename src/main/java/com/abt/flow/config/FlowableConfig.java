package com.abt.flow.config;

import com.abt.flow.listener.GlobalTaskCompleteListener;
import com.abt.flow.listener.ProcessDeleteListener;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 流程相关配置
 */
@Configuration
@Slf4j
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private final GlobalTaskCompleteListener globalTaskCompleteListener;
    private final ProcessDeleteListener processDeleteListener;


    private final FlowableDataSourceConfigurer configurer;
    public FlowableConfig(GlobalTaskCompleteListener globalTaskCompleteListener, ProcessDeleteListener processDeleteListener, FlowableDataSourceConfigurer configurer) {
        this.globalTaskCompleteListener = globalTaskCompleteListener;
        this.processDeleteListener = processDeleteListener;
        this.configurer = configurer;
    }

    /**
     * 导入自定义配置数据源 FlowableDataSourceConfigurer
     * !!==> 不要设置事件Log:engineConfiguration.setEnableDatabaseEventLogging(true); 异步任务容易出现死锁
     * @param engineConfiguration
     */
    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        log.info("将自定义配置导入SpringProcessEngineConfiguration");
        //数据源
        engineConfiguration.addConfigurator(configurer);
        //按类型设置监听器
        Map<String, List<FlowableEventListener>> typedListeners =
                Map.of(FlowableEngineEventType.TASK_COMPLETED.name(), List.of(globalTaskCompleteListener),
                        FlowableEngineEventType.PROCESS_CANCELLED.name(), List.of(processDeleteListener));
        engineConfiguration.setTypedEventListeners(typedListeners);
    }





}
