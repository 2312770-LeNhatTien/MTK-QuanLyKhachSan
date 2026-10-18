package vn.edu.dlu.khachsan.scheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.dlu.khachsan.service.DatPhongService;
@Component
@ConditionalOnProperty(name="app.scheduler.enabled",havingValue="true",matchIfMissing=true)
public class HuyDonQuaHanJob {
    private final DatPhongService datPhongService;
    public HuyDonQuaHanJob(DatPhongService d){datPhongService=d;}
    @Scheduled(fixedRate=60000)
    public void chay(){datPhongService.huyDonQuaHan();}
}
