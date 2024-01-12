package group.idealworld.dew.example.hbase;

import group.idealworld.dew.core.hbase.HBaseTemplate;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Cluster example initiator.
 *
 * @author 迹_Jason
 */
@Component
public class HBaseExampleInitiator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseExampleInitiator.class);

    @Autowired
    private HBaseTemplate hbaseTemplate;

    /**
     * Init.
     *
     * @throws Exception the exception
     */
    @PostConstruct
    public void init() throws Exception {
        String st = hbaseTemplate.get("DMP:D10_DOP.FDN.V2.T_APP_USER", "0002093140000000",
                "0", "reg_platform", (result, row) -> Bytes.toString(result.value()));
        LOGGER.info("result:{}", st);
    }

}
