package com.example.fallanddead;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> implements OnPersonItemClickListener {

    ArrayList<PersonInfo> items=new ArrayList<PersonInfo>();
    OnPersonItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView icon;
        public ViewHolder(@NonNull  View itemView,final OnPersonItemClickListener listener){
            super(itemView);
            textView=itemView.findViewById(R.id.textView9);
            icon=itemView.findViewById(R.id.imageView2);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    int position=getAdapterPosition();
                    if(listener!=null){

                        listener.onItemClick(ViewHolder.this,v,position);
                    }
                }
            });
        }


        public void setItem(PersonInfo item){
            textView.setText(item.getName());

            if(item.getUrl().equals("")) {
                icon.setImageResource(R.drawable.sim);
            }
            else{
                // 이미지 가져와서 그거 띄움
                icon.setImageURI(Uri.parse(item.getUrl()));
            }

        }

    } // ViewHolder 클래스 끝


    public void setItems(ArrayList<PersonInfo> items){this.items=items;}
    public PersonInfo getItem(int position){ return items.get(position);} // 배열의 몇번째 요소인지
    public void addItem(PersonInfo item){
        items.add(item);
    }


    // 리스너 설정
    public void setOnItemClickListener(OnPersonItemClickListener listener){
        this.listener=listener;
    }

    // 아이템 눌렀을때
    @Override
    public void onItemClick(ViewHolder holder, View view, int position){
        if(listener!=null){
            listener.onItemClick(holder,view,position);
        }
    }

    // 뷰홀더 객체가 만들어질때 자동으로 호출
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        View itemView=inflater.inflate(R.layout.cardview_layout,viewGroup,false);
        return new ViewHolder(itemView,this);
    }

    // 뷰홀더 객체가 재사용될때 자동으로 호출
    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.ViewHolder viewHolder, int position) {
        PersonInfo item=items.get(position);
        viewHolder.setItem(item);

    }

    // 어댑터에서 관리하는 아이템 개수 반환
    @Override
    public int getItemCount() {
        return items.size();
    }
}
