package vn.edu.dlu.khachsan.exception;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
@ControllerAdvice
public class XuLyLoiChung {
    @ExceptionHandler(NghiepVuException.class)
    public Object nghiepVu(NghiepVuException ex,HttpServletRequest request){
        if(request.getRequestURI().endsWith("/tam-tinh"))return ResponseEntity.badRequest().body(Map.of("thongBao",ex.getMessage()));
        ModelAndView view=new ModelAndView("error/nghiep-vu");view.setStatus(HttpStatus.BAD_REQUEST);
        view.addObject("thongBaoLoi",ex.getMessage());return view;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> khongHopLe(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body(Map.of("thongBao",ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage()));
    }
}

