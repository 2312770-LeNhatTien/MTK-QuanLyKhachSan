param([string]$TenContainer = 'khachsan-mysql')

$ErrorActionPreference = 'Stop'
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$script:soKiemTra = 0

function Assert-Dung($DieuKien, [string]$MoTa) {
    if (-not $DieuKien) { throw "Không đạt: $MoTa" }
    $script:soKiemTra++
}

function Invoke-Sql([string]$Sql, [string]$MaLoiMongDoi = '') {
    $ketQua = @($Sql | & docker exec -i $TenContainer sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql --default-character-set=utf8mb4 -uroot -N -B --raw quan_ly_khach_san' 2>&1)
    $maThoat = $LASTEXITCODE
    $noiDung = $ketQua -join "`n"
    if ($MaLoiMongDoi) {
        Assert-Dung (($maThoat -ne 0) -and ($noiDung -match "ERROR $MaLoiMongDoi ")) "MySQL từ chối dữ liệu với lỗi $MaLoiMongDoi"
        return
    }
    if ($maThoat -ne 0) { throw "Lỗi chạy SQL: $noiDung" }
    return $ketQua
}

# Danh sách độc lập theo Mục 4: tên cột, kiểu dữ liệu, khả năng nhận NULL.
$bangMongDoi = [ordered]@{
    VAI_TRO = 'MaVaiTro:varchar(10):NO TenVaiTro:varchar(50):NO'
    NHAN_VIEN = 'MaNV:varchar(10):NO HoTen:varchar(100):NO Email:varchar(100):NO SDT:varchar(15):YES MatKhau:varchar(255):NO MaVaiTro:varchar(10):NO'
    KHACH_HANG = 'MaKH:varchar(10):NO HoTen:varchar(100):NO Email:varchar(100):NO SDT:varchar(15):YES MatKhau:varchar(255):NO DiaChi:varchar(200):YES NgayTao:datetime:NO'
    LOAI_PHONG = 'MaLoaiPhong:varchar(10):NO TenLoaiPhong:varchar(50):NO GiaMoiDem:decimal(12,2):NO SucChua:int:NO MoTa:varchar(255):YES'
    PHONG = 'MaPhong:varchar(10):NO SoPhong:varchar(10):NO Tang:int:YES TrangThai:varchar(20):NO MaLoaiPhong:varchar(10):NO'
    DAT_PHONG = 'MaDatPhong:varchar(10):NO NgayDat:datetime:NO NgayNhanPhong:date:NO NgayTraPhong:date:NO TrangThai:varchar(20):NO TongTien:decimal(12,2):NO MaKH:varchar(10):NO MaNV:varchar(10):YES'
    CHI_TIET_DAT_PHONG = 'MaCTDP:varchar(10):NO SoLuongKhach:int:NO GiaThoiDiemDat:decimal(12,2):NO MaDatPhong:varchar(10):NO MaPhong:varchar(10):NO'
    DICH_VU = 'MaDichVu:varchar(10):NO TenDichVu:varchar(100):NO DonGia:decimal(12,2):NO CachTinh:varchar(20):NO DonViTinh:varchar(30):NO MoTa:varchar(255):YES'
    CHI_TIET_DICH_VU = 'MaCTDV:varchar(10):NO SoLuong:int:NO DonGiaThoiDiemDat:decimal(12,2):NO MaDatPhong:varchar(10):NO MaDichVu:varchar(10):NO'
    HOA_DON = 'MaHoaDon:varchar(10):NO NgayLap:datetime:NO TongTien:decimal(12,2):NO TrangThaiThanhToan:varchar(20):NO MaDatPhong:varchar(10):NO'
    THANH_TOAN = 'MaThanhToan:varchar(10):NO PhuongThuc:varchar(20):NO SoTien:decimal(12,2):NO NgayThanhToan:datetime:NO TrangThai:varchar(20):NO GhiChu:varchar(200):YES MaHoaDon:varchar(10):NO'
    DANH_GIA = 'MaDanhGia:varchar(10):NO SoSao:int:NO NoiDung:varchar(500):YES NgayDanhGia:datetime:NO MaDatPhong:varchar(10):NO'
    THAM_SO = 'MaThamSo:varchar(30):NO TenThamSo:varchar(100):NO GiaTri:decimal(12,2):NO DonVi:varchar(20):YES'
}

$bangThucTe = @(Invoke-Sql "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME <> 'flyway_schema_history' ORDER BY TABLE_NAME;")
Assert-Dung (($bangThucTe -join ',') -ceq (($bangMongDoi.Keys | Sort-Object) -join ',')) 'Đúng 13 tên bảng nghiệp vụ'

$macDinhMongDoi = @{
    'KHACH_HANG.NgayTao' = 'CURRENT_TIMESTAMP'
    'PHONG.TrangThai' = 'TRONG'
    'DAT_PHONG.NgayDat' = 'CURRENT_TIMESTAMP'
    'DAT_PHONG.TrangThai' = 'CHO_THANH_TOAN'
    'DAT_PHONG.TongTien' = '0.00'
    'HOA_DON.NgayLap' = 'CURRENT_TIMESTAMP'
    'HOA_DON.TrangThaiThanhToan' = 'CHUA_THANH_TOAN'
    'THANH_TOAN.NgayThanhToan' = 'CURRENT_TIMESTAMP'
    'THANH_TOAN.TrangThai' = 'DANG_XU_LY'
    'DANH_GIA.NgayDanhGia' = 'CURRENT_TIMESTAMP'
}

$cotThucTe = @(Invoke-Sql "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COALESCE(COLUMN_DEFAULT, '<NULL>') FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME <> 'flyway_schema_history' ORDER BY TABLE_NAME, ORDINAL_POSITION;")
Assert-Dung ($cotThucTe.Count -eq 70) 'Đủ 70 cột'
foreach ($tenBang in $bangMongDoi.Keys) {
    $cacCot = @($cotThucTe | Where-Object { ($_ -split "`t")[0] -ceq $tenBang })
    $chuKy = @($cacCot | ForEach-Object { $p = $_ -split "`t"; "$($p[1]):$($p[2]):$($p[3])" }) -join ' '
    Assert-Dung ($chuKy -ceq $bangMongDoi[$tenBang]) "Tên cột, kiểu và NULL của $tenBang"
    foreach ($cot in $cacCot) {
        $p = $cot -split "`t"
        $khoa = "$tenBang.$($p[1])"
        $mongDoi = if ($macDinhMongDoi.ContainsKey($khoa)) { $macDinhMongDoi[$khoa] } else { '<NULL>' }
        $thucTe = $p[4] -replace '(?i)current_timestamp\(\)', 'CURRENT_TIMESTAMP'
        Assert-Dung ($thucTe -ceq $mongDoi) "Giá trị mặc định $khoa"
    }
}

$dinhDang = @(Invoke-Sql "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME <> 'flyway_schema_history' AND ENGINE='InnoDB' AND TABLE_COLLATION='utf8mb4_unicode_ci';")
Assert-Dung ($dinhDang.Count -eq 13) '13 bảng dùng InnoDB và utf8mb4_unicode_ci'

$khoaChinh = @(Invoke-Sql "SELECT TABLE_NAME, COLUMN_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA=DATABASE() AND CONSTRAINT_NAME='PRIMARY' AND TABLE_NAME <> 'flyway_schema_history';")
Assert-Dung ($khoaChinh.Count -eq 13) 'Đủ 13 khóa chính'
foreach ($tenBang in $bangMongDoi.Keys) {
    $cotDau = ($bangMongDoi[$tenBang] -split ':')[0]
    Assert-Dung ($khoaChinh -ccontains "$tenBang`t$cotDau") "Khóa chính $tenBang.$cotDau"
}

$duyNhatMongDoi = @('VAI_TRO:TenVaiTro', 'NHAN_VIEN:Email', 'KHACH_HANG:Email', 'LOAI_PHONG:TenLoaiPhong', 'PHONG:SoPhong', 'CHI_TIET_DAT_PHONG:MaDatPhong,MaPhong', 'DICH_VU:TenDichVu', 'CHI_TIET_DICH_VU:MaDatPhong,MaDichVu', 'HOA_DON:MaDatPhong', 'DANH_GIA:MaDatPhong')
$duyNhat = @(Invoke-Sql "SELECT CONCAT(k.TABLE_NAME, ':', GROUP_CONCAT(k.COLUMN_NAME ORDER BY k.ORDINAL_POSITION SEPARATOR ',')) FROM information_schema.KEY_COLUMN_USAGE k JOIN information_schema.TABLE_CONSTRAINTS c ON k.CONSTRAINT_SCHEMA=c.CONSTRAINT_SCHEMA AND k.TABLE_NAME=c.TABLE_NAME AND k.CONSTRAINT_NAME=c.CONSTRAINT_NAME WHERE k.TABLE_SCHEMA=DATABASE() AND c.CONSTRAINT_TYPE='UNIQUE' GROUP BY k.TABLE_NAME,k.CONSTRAINT_NAME;")
Assert-Dung (($duyNhat | Sort-Object) -join '|' -ceq (($duyNhatMongDoi | Sort-Object) -join '|')) 'Đủ 10 ràng buộc UNIQUE đúng cột'

$khoaNgoaiMongDoi = @('NHAN_VIEN.MaVaiTro>VAI_TRO.MaVaiTro:RESTRICT', 'PHONG.MaLoaiPhong>LOAI_PHONG.MaLoaiPhong:RESTRICT', 'DAT_PHONG.MaKH>KHACH_HANG.MaKH:RESTRICT', 'DAT_PHONG.MaNV>NHAN_VIEN.MaNV:RESTRICT', 'CHI_TIET_DAT_PHONG.MaDatPhong>DAT_PHONG.MaDatPhong:RESTRICT', 'CHI_TIET_DAT_PHONG.MaPhong>PHONG.MaPhong:RESTRICT', 'CHI_TIET_DICH_VU.MaDatPhong>DAT_PHONG.MaDatPhong:RESTRICT', 'CHI_TIET_DICH_VU.MaDichVu>DICH_VU.MaDichVu:RESTRICT', 'HOA_DON.MaDatPhong>DAT_PHONG.MaDatPhong:RESTRICT', 'THANH_TOAN.MaHoaDon>HOA_DON.MaHoaDon:RESTRICT', 'DANH_GIA.MaDatPhong>DAT_PHONG.MaDatPhong:RESTRICT')
$khoaNgoai = @(Invoke-Sql "SELECT CONCAT(k.TABLE_NAME,'.',k.COLUMN_NAME,'>',k.REFERENCED_TABLE_NAME,'.',k.REFERENCED_COLUMN_NAME,':',r.DELETE_RULE) FROM information_schema.KEY_COLUMN_USAGE k JOIN information_schema.REFERENTIAL_CONSTRAINTS r ON k.CONSTRAINT_SCHEMA=r.CONSTRAINT_SCHEMA AND k.CONSTRAINT_NAME=r.CONSTRAINT_NAME AND k.TABLE_NAME=r.TABLE_NAME WHERE k.TABLE_SCHEMA=DATABASE() AND k.REFERENCED_TABLE_NAME IS NOT NULL;")
Assert-Dung (($khoaNgoai | Sort-Object) -join '|' -ceq (($khoaNgoaiMongDoi | Sort-Object) -join '|')) 'Đủ 11 khóa ngoại đúng cột, đều ON DELETE RESTRICT'

$checkMongDoi = @{
    CK_LOAI_PHONG_GiaMoiDem = 'GiaMoiDem > 0'
    CK_LOAI_PHONG_SucChua = 'SucChua > 0'
    CK_PHONG_TrangThai = "TrangThai IN ('TRONG','CO_KHACH','DANG_DON','BAO_TRI')"
    CK_DAT_PHONG_Ngay = 'NgayTraPhong > NgayNhanPhong'
    CK_DAT_PHONG_TrangThai = "TrangThai IN ('CHO_THANH_TOAN','DA_XAC_NHAN','DA_HUY','HOAN_TAT')"
    CK_CHI_TIET_DAT_PHONG_SoLuongKhach = 'SoLuongKhach > 0'
    CK_DICH_VU_DonGia = 'DonGia > 0'
    CK_DICH_VU_CachTinh = "CachTinh IN ('THEO_DEM','MOT_LAN')"
    CK_CHI_TIET_DICH_VU_SoLuong = 'SoLuong BETWEEN 1 AND 10'
    CK_HOA_DON_TrangThaiThanhToan = "TrangThaiThanhToan IN ('CHUA_THANH_TOAN','DA_COC','DA_HOAN_TAT')"
    CK_THANH_TOAN_PhuongThuc = "PhuongThuc IN ('TRUC_TUYEN','TIEN_MAT','THE')"
    CK_THANH_TOAN_SoTien = 'SoTien > 0'
    CK_THANH_TOAN_TrangThai = "TrangThai IN ('DANG_XU_LY','THANH_CONG','THAT_BAI')"
    CK_DANH_GIA_SoSao = 'SoSao BETWEEN 1 AND 5'
}
function Convert-ChuanHoa([string]$BieuThuc) {
    $BieuThuc = $BieuThuc.Replace("\'", "'")
    return ($BieuThuc -replace '_utf8mb4', '' -replace '[\s`()]+', '').ToUpperInvariant()
}
$cacCheck = @(Invoke-Sql "SELECT c.CONSTRAINT_NAME,c.CHECK_CLAUSE,t.ENFORCED FROM information_schema.CHECK_CONSTRAINTS c JOIN information_schema.TABLE_CONSTRAINTS t ON c.CONSTRAINT_SCHEMA=t.CONSTRAINT_SCHEMA AND c.CONSTRAINT_NAME=t.CONSTRAINT_NAME WHERE c.CONSTRAINT_SCHEMA=DATABASE();")
Assert-Dung ($cacCheck.Count -eq 14) 'Đủ 14 CHECK'
foreach ($check in $cacCheck) {
    $p = $check -split "`t"
    Assert-Dung ($checkMongDoi.ContainsKey($p[0])) "CHECK đã quy định: $($p[0])"
    Assert-Dung ((Convert-ChuanHoa $p[1]) -ceq (Convert-ChuanHoa $checkMongDoi[$p[0]])) "Nội dung $($p[0])"
    Assert-Dung ($p[2] -ceq 'YES') "CHECK đang được thực thi: $($p[0])"
}

# Dữ liệu thử nằm trong transaction, tự rollback cả khi mysql dừng vì lỗi.
$duLieuThu = @'
START TRANSACTION;
INSERT INTO LOAI_PHONG (MaLoaiPhong, TenLoaiPhong, GiaMoiDem, SucChua) VALUES ('ZZLPTEST', 'Kiem tra CSDL', 500000, 2);
INSERT INTO PHONG (MaPhong, SoPhong, MaLoaiPhong) VALUES ('ZZPTEST', 'ZZTEST', 'ZZLPTEST');
'@
Invoke-Sql ($duLieuThu + "`nUPDATE LOAI_PHONG SET GiaMoiDem=0 WHERE MaLoaiPhong='ZZLPTEST';") '3819'
Invoke-Sql ($duLieuThu + "`nUPDATE LOAI_PHONG SET SucChua=0 WHERE MaLoaiPhong='ZZLPTEST';") '3819'
Invoke-Sql ($duLieuThu + "`nUPDATE PHONG SET TrangThai='SAI' WHERE MaPhong='ZZPTEST';") '3819'
Invoke-Sql ($duLieuThu + "`nINSERT INTO PHONG (MaPhong,SoPhong,MaLoaiPhong) VALUES ('ZZPTEST2','ZZTEST','ZZLPTEST');") '1062'
Invoke-Sql ($duLieuThu + "`nUPDATE PHONG SET MaLoaiPhong='ZZKHONGCO' WHERE MaPhong='ZZPTEST';") '1452'
Invoke-Sql ($duLieuThu + "`nDELETE FROM LOAI_PHONG WHERE MaLoaiPhong='ZZLPTEST';") '1451'
$macDinhPhong = @(Invoke-Sql ($duLieuThu + "`nSELECT TrangThai FROM PHONG WHERE MaPhong='ZZPTEST'; ROLLBACK;"))
Assert-Dung (($macDinhPhong -join '') -ceq 'TRONG') 'Phòng mới nhận mặc định TRONG'
$conDuLieu = @(Invoke-Sql "SELECT (SELECT COUNT(*) FROM LOAI_PHONG WHERE MaLoaiPhong='ZZLPTEST') + (SELECT COUNT(*) FROM PHONG WHERE MaPhong='ZZPTEST');")
Assert-Dung (($conDuLieu -join '') -eq '0') 'Dữ liệu thử đã rollback hết'

Write-Output "ĐẠT: $script:soKiemTra kiểm tra CSDL (13 bảng, 70 cột, 13 PK, 10 UNIQUE, 11 FK RESTRICT, 14 CHECK; dữ liệu thử đã rollback)."
