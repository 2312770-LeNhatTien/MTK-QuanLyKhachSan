(() => {
 const canvas=document.getElementById('bieuDoDoanhThu');if(!canvas||!window.Chart)return;
 const rows=[...document.querySelectorAll('#bangDoanhThu tbody tr')];
 new Chart(canvas,{type:'bar',data:{labels:rows.map(r=>r.dataset.ngay),datasets:[{label:'Doanh thu (đ)',data:rows.map(r=>Number(r.dataset.tien)),backgroundColor:'#356354',borderRadius:4}]},options:{responsive:true,maintainAspectRatio:false,plugins:{legend:{display:false},tooltip:{callbacks:{label:ctx=>new Intl.NumberFormat('vi-VN').format(ctx.parsed.y)+' đ'}}},scales:{y:{beginAtZero:true,ticks:{callback:n=>new Intl.NumberFormat('vi-VN').format(n)}}}}});
})();
