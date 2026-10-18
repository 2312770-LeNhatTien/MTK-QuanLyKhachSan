package vn.edu.dlu.khachsan.service;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.edu.dlu.khachsan.dto.ChiTietDonDto;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
@Service
public class EmailLogService {
    private final TemplateEngine templateEngine;
    private final Path thuMuc;
    public EmailLogService(TemplateEngine e,@Value("$"+"{app.email-log-dir:email-logs}") String dir){
        templateEngine=e;thuMuc=Path.of(dir).toAbsolutePath().normalize();
    }
    public void ghiXacNhan(ChiTietDonDto ct){
        Context c=new Context(Locale.forLanguageTag("vi-VN"));c.setVariable("ct",ct);
        c.setVariable("dinhDang",new vn.edu.dlu.khachsan.util.DinhDang());
        try{
            Files.createDirectories(thuMuc);
            Files.writeString(thuMuc.resolve("xac-nhan-"+ct.don().getMaDatPhong()+".html"),
                    templateEngine.process("email/xac-nhan",c),StandardCharsets.UTF_8);
        }catch(IOException ex){throw new NghiepVuException("Chưa thể ghi email xác nhận. Vui lòng thử lại.");}
    }
}
