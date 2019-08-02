package com.example.chat_app.Adabter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chat_app.Holder.QBunreadMssageHolder;
import com.example.chat_app.R;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

public class ChatDialogAdabter extends BaseAdapter {
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;

    public ChatDialogAdabter(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }

    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {

        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_chat_dialog , null);

            TextView txttitle , txtmessage;
            ImageView imageView , image_unread;

            txttitle = (TextView)view.findViewById(R.id.list_chatDialog_title);
            txtmessage = (TextView)view.findViewById(R.id.list_chatDialog_message);
            imageView = (ImageView)view.findViewById(R.id.image_chatDialog);
            image_unread = (ImageView)view.findViewById(R.id.image_unread);

            txttitle.setText(qbChatDialogs.get(position).getName());
            txtmessage.setText(qbChatDialogs.get(position).getLastMessage());

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomcolor = generator.getRandomColor();

            TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();

            //get first character from list chat Dialog title to create chat Dialog image
            TextDrawable drawable = builder.build(txttitle.getText().toString().substring(0 ,1).toUpperCase(),randomcolor);
            imageView.setImageDrawable(drawable);

            // set message unread count
            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();
            int unread_count = QBunreadMssageHolder.getInstance().getBundle().getInt(qbChatDialogs.get(position).getDialogId());
            if (unread_count > 0){
                TextDrawable unreaddrawable = unreadBuilder.build(""+unread_count, Color.RED);
                image_unread.setImageDrawable(unreaddrawable);

            }

        }
        return view;
    }
}
