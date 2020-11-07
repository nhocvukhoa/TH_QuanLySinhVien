package com.example.quanlysinhvien;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    ListView lsvSinhVien;
    Dialog dialog;
    viewDialog viewDialog;
    SinhVienAdapter adapter;
    TuongTacDatabase database;
    int h;
    SinhVien sv;
    boolean them=true,sua=false,xoa=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lsvSinhVien=(ListView)findViewById(R.id.lsvSinhVien);

        database=new TuongTacDatabase(this);

        getDATA();

        adapter=new SinhVienAdapter(this,0,data.getDt().arrSV);
        lsvSinhVien.setAdapter(adapter);

        dialog= new Dialog(this);
        dialog.setContentView(R.layout.activitytsx);
        viewDialog=new viewDialog(dialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.idTrangChu:
                break;
            case R.id.idQuanLy:
                them=false;
                sua=true;
                xoa=true;

                viewDialog.setbtn1();

                Toast.makeText(MainActivity.this,"Chon SV muon sua",Toast.LENGTH_SHORT).show();

                lsvSinhVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                        sv=adapter.getItem(i);
                        h=i;
                        viewDialog.setTT(sv);
                        dialog.show();
                    }
                });
                break;
            case R.id.idTimKiem:
                break;
            case R.id.idDKKHoc:
                them=true;
                sua=false;
                xoa=false;
                viewDialog.setbtn1();
                lsvSinhVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        
                    }
                });
                dialog.show();
                break;
        }
        return  super.onOptionsItemSelected(item);
    }
    public void getDATA(){
        database.open();
        data.getDt().arrSV= new ArrayList<>(database.getALLdata());
        database.close();
    }
    class viewDialog{
        EditText edtTen,edtSdt,edtGmail;
        Button btnNgaySinh,btnThem,btnSua,btnXoa;
        Spinner splLop;
        String lop;
        String gt="nam";
        RadioGroup GT;

        public viewDialog(Dialog v){
            edtTen=(EditText)v.findViewById(R.id.edtTen);
            edtSdt=(EditText)v.findViewById(R.id.edtSDT);
            edtGmail=(EditText)v.findViewById(R.id.edtEmail);

            btnXoa=(Button)v.findViewById(R.id.xoa);
            btnSua=(Button)v.findViewById(R.id.sua);
            btnThem=(Button)v.findViewById(R.id.btnThem);

            splLop=(Spinner)v.findViewById(R.id.splLop);

            GT=(RadioGroup)v.findViewById(R.id.RGGT);
            setRS();
            setBtn();
        }
        public void setbtn1(){
            btnThem.setEnabled(them);
            btnXoa.setEnabled(xoa);
            btnSua.setEnabled(sua);
        }
        public void setTT(SinhVien sv){
            edtTen.setText(sv.getTen());
            edtSdt.setText(sv.getSodt());
            edtGmail.setText(sv.getEmail());

            if(sv.getGioitinh().equals("nu")){
                GT.check(R.id.nu);
            }else{
                GT.check(R.id.nam);
            }
            int c=0;
            for(int i=0;i<getResources().getStringArray(R.array.lop).length;i++){
                if(sv.getLophoc().equals(getResources().getStringArray(R.array.lop)[i])){
                    c=i;
                    break;
                }
            }
            splLop.setSelection(c);
        }
        private void setBtn(){
            btnThem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GT.getCheckedRadioButtonId() == R.id.nam){
                        gt = "nam";
                    }else {
                        gt = "nu";
                    }
                    data.getDt().arrSV.add(getSV());
                    database.open();
                    database.themSV(getSV());
                    database.close();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();

                }
            });
            btnSua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GT.getCheckedRadioButtonId() == R.id.nam){
                        gt = "nam";
                    }else {
                        gt = "nu";
                    }
                    SinhVien s = getSV();
                    s.setId(sv.getId());
                    data.getDt().arrSV.set(h,s);
                    database.open();
                    database.suaSV(s);
                    database.close();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            btnXoa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.getDt().arrSV.remove(h);
                    database.open();
                    database.xoaSV(data.getDt().arrSV.get(h).getId());
                    database.close();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        }
        private void setRS(){
            splLop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    lop=getResources().getStringArray(R.array.lop)[i];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    lop=getResources().getStringArray(R.array.lop)[0];
                }
            });

        }
        private SinhVien getSV(){
            SinhVien sv= new SinhVien();
            sv.setTen(edtTen.getText().toString());
            sv.setEmail(edtGmail.getText().toString());
            sv.setSodt(edtSdt.getText().toString());
            sv.setLophoc(lop);
            sv.setGioitinh(gt);
            return sv;

        }
    }
}