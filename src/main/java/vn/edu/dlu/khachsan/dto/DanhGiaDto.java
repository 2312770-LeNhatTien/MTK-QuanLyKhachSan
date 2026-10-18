package vn.edu.dlu.khachsan.dto;
import jakarta.validation.constraints.*;
public class DanhGiaDto {
 @Min(value=1,message="Vui lòng chọn từ 1 đến 5 sao.") @Max(value=5,message="Vui lòng chọn từ 1 đến 5 sao.")
 private int soSao=5;
 @Size(max=500,message="Nội dung tối đa 500 ký tự.")
 private String noiDung;
 public int getSoSao(){return soSao;} public void setSoSao(int v){soSao=v;}
 public String getNoiDung(){return noiDung;} public void setNoiDung(String v){noiDung=v;}
}
