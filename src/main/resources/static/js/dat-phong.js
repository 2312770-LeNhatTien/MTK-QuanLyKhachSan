"use strict";
document.querySelectorAll('form[data-chon-phong]').forEach(form=>{
    const boxes=[...form.querySelectorAll('input[name="maPhong"]')];
    if(!boxes.length)return;
    const validate=()=>boxes[0].setCustomValidity(boxes.some(b=>b.checked)?'':'Vui lòng chọn ít nhất một phòng.');
    boxes.forEach(b=>b.addEventListener('change',validate));validate();
});
const datPhong=document.getElementById('formDatPhong');
if(datPhong){
    let controller;
    const money=v=>new Intl.NumberFormat('vi-VN',{maximumFractionDigits:2}).format(v)+' đ';
    const update=async()=>{
        if(controller)controller.abort();controller=new AbortController();
        const dto={ngayNhanPhong:datPhong.elements.ngayNhanPhong.value,ngayTraPhong:datPhong.elements.ngayTraPhong.value,phong:{},dichVu:{}};
        new FormData(datPhong).forEach((v,k)=>{const m=k.match(/^(phong|dichVu)\[([^\]]+)\]$/);if(m)dto[m[1]][m[2]]=Number(v);});
        const btn=document.getElementById('xacNhanDatPhong'),err=document.getElementById('loiTamTinh');btn.disabled=true;
        try{
            const token=document.querySelector('meta[name="_csrf"]').content,header=document.querySelector('meta[name="_csrf_header"]').content;
            const response=await fetch('/dat-phong/tam-tinh',{method:'POST',headers:{'Content-Type':'application/json','Accept':'application/json',[header]:token},body:JSON.stringify(dto),signal:controller.signal});
            const data=await response.json();if(!response.ok)throw new Error(data.thongBao||'Không thể cập nhật tạm tính.');
            const body=document.getElementById('dongChiPhi');body.replaceChildren();
            data.chiTiet.forEach(d=>{const tr=document.createElement('tr');[d.noiDung,d.cachTinh,money(d.thanhTien)].forEach((v,i)=>{const td=document.createElement('td');td.textContent=v;if(i===2)td.className='text-end';tr.append(td);});body.append(tr);});
            document.getElementById('tongChiPhi').textContent=money(data.tongTien);err.classList.add('d-none');btn.disabled=false;
        }catch(e){if(e.name==='AbortError')return;err.textContent=e.message||'Chưa cập nhật được tạm tính.';err.classList.remove('d-none');}
    };
    datPhong.querySelectorAll('.chonDichVu').forEach(box=>box.addEventListener('change',()=>{document.getElementById(box.dataset.soLuong).disabled=!box.checked;}));
    datPhong.addEventListener('change',update);
}
const thanhToan=document.getElementById('formThanhToan');
if(thanhToan){const mode=thanhToan.elements.toanBo,amount=thanhToan.elements.soTien;const update=()=>{amount.disabled=mode.value==='true';amount.required=!amount.disabled;};mode.addEventListener('change',update);update();}
const countdown=document.getElementById('demNguoc');
if(countdown){const update=()=>{const left=Math.max(0,Math.floor((new Date(countdown.dataset.hetHan).getTime()-Date.now())/1000));countdown.textContent=left?Math.floor(left/60)+':'+String(left%60).padStart(2,'0'):'Đã hết thời gian';if(!left&&thanhToan)thanhToan.querySelectorAll('input,select,button').forEach(e=>e.disabled=true);};update();setInterval(update,1000);}
