package wallet.vayu.com.android_wallet.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wallet.vayu.com.android_wallet.R;

public class RecyclerAdapter extends StackAdapter<Integer> {

    public RecyclerAdapter(Context context) {
        super(context);
    }



    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemLargeHeaderViewHolder) {
            ColorItemLargeHeaderViewHolder h = (ColorItemLargeHeaderViewHolder) holder;
            h.onBind(data, position);
        }
        if (holder instanceof ColorItemWithNoHeaderViewHolder) {
            ColorItemWithNoHeaderViewHolder h = (ColorItemWithNoHeaderViewHolder) holder;
            h.onBind(data, position);
        }
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case R.layout.aadhar_list_card:
                view = getLayoutInflater().inflate(R.layout.aadhar_list_card, parent, false);
                return new ColorItemLargeHeaderViewHolder(view);
            case R.layout.pan_list_card:
                view = getLayoutInflater().inflate(R.layout.pan_list_card, parent, false);
                return new ColorItemWithNoHeaderViewHolder(view);
            default:
                view = getLayoutInflater().inflate(R.layout.user_list_adapter, parent, false);
                return new ColorItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.aadhar_list_card;
        } else if (position == 1) {
            return R.layout.pan_list_card;
        } else {
            return R.layout.user_list_adapter;
        }
    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle, date, place, time,audi_num;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            date = (TextView) view.findViewById(R.id.date);
            place = (TextView) view.findViewById(R.id.place);
            time = (TextView) view.findViewById(R.id.time);
            audi_num = (TextView) view.findViewById(R.id.tv_Audi_num);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
            place.setVisibility(b ? View.VISIBLE : View.GONE);
            time.setVisibility(b ? View.VISIBLE : View.GONE);
            audi_num.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(Integer data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            StringBuilder smsBuilder = new StringBuilder();
            final String SMS_URI_INBOX = "content://sms/inbox";
            final String SMS_URI_ALL = "content://sms/";
            try {
                Uri uri = Uri.parse(SMS_URI_INBOX);
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                Cursor cur = getContext().getContentResolver().query(uri, projection, "address='AD-BMSHOW'", null, "date desc");
                if (cur.moveToFirst()) {
                    int index_Address = cur.getColumnIndex("address");
                    int index_Person = cur.getColumnIndex("person");
                    int index_Body = cur.getColumnIndex("body");
                    int index_Date = cur.getColumnIndex("date");
                    int index_Type = cur.getColumnIndex("type");
                    do {
                        String strAddress = cur.getString(index_Address);
                        int intPerson = cur.getInt(index_Person);

                        String strbody = cur.getString(index_Body);
                        String locality;
                        if (strbody.contains("Hyderabad")) {
                            locality = "Hyderabad";
                            place.setText(locality);
                        } else {
                            locality = "locality";
                            place.setText(locality);
                        }
                        String audit_no = "AUDI";
                        if (strbody.contains("AUDI")) {
                            String rep = strbody.replace(")", "");
                            String Audi = rep.substring(rep.indexOf(audit_no) + audit_no.length());
                            audi_num.setText("AUDI :" +Audi);
                        } else {
                            audi_num.setText("NULL");
                        }
                        Long longDate = cur.getLong(index_Date);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(longDate);
                        Date dte = new Date(longDate.longValue());
                        String formattedDate = new SimpleDateFormat("MM/dd/yyyy").format(dte);
                        int int_Type = cur.getInt(index_Type);
                        smsBuilder.append("[ ");
                        smsBuilder.append(strAddress + ", ");
                        smsBuilder.append(intPerson + ", ");
                        smsBuilder.append(strbody + ", ");
                        smsBuilder.append(longDate + ", ");
                        smsBuilder.append(int_Type);
                        smsBuilder.append(" ]\n\n");
                        mTextTitle.setText("Book my show");
                        date.setText(formattedDate);
                    } while (cur.moveToNext());

                    if (!cur.isClosed()) {
                        cur.close();
                        cur = null;
                    }
                } else {
                    smsBuilder.append("no result!");
                } // end if
            } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }
        }
    }

    static class ColorItemWithNoHeaderViewHolder extends CardStackView.ViewHolder {
        private final ImageView iv_pan_img;
        View mLayout;
        TextView tv_num_pan, tv_pan_name;

        public ColorItemWithNoHeaderViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            tv_num_pan = (TextView) view.findViewById(R.id.tv_pan_num);
            tv_pan_name = (TextView) view.findViewById(R.id.tv_pan_name);
            iv_pan_img = (ImageView) view.findViewById(R.id.pan_img);
        }

        @Override
        public void onItemExpand(boolean b) {
            tv_pan_name.setVisibility(b ? View.VISIBLE : View.GONE);
            tv_num_pan.setVisibility(b ? View.VISIBLE : View.GONE);
            iv_pan_img.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(Integer data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            SharedPreferences sharedpref;
            sharedpref = getContext().getSharedPreferences("MyPref", 0);
            String pan_num = sharedpref.getString("pan_num", "");
            String pan_name = sharedpref.getString("pan_name", "");
            String pan_img = sharedpref.getString("pan_img", "");
            byte[] decodedString = Base64.decode(pan_img, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            iv_pan_img.setImageBitmap(decodedByte);
            tv_num_pan.setText(pan_num);
            tv_pan_name.setText(pan_name);
        }

    }

    static class ColorItemLargeHeaderViewHolder extends CardStackView.ViewHolder {
        private ImageView AAd_img;
        View mLayout;
        TextView AAd_num, AAd_name, AAd_address;

        public ColorItemLargeHeaderViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.aadhar_view);
            AAd_num = (TextView) view.findViewById(R.id.card_num_aad);
            AAd_name = (TextView) view.findViewById(R.id.tv_aad_name);
            AAd_address = (TextView) view.findViewById(R.id.tv_address);
            AAd_img = (ImageView) view.findViewById(R.id.aadhar_img);
        }

        @Override
        public void onItemExpand(boolean b) {
            AAd_name.setVisibility(b ? View.VISIBLE : View.GONE);
            AAd_address.setVisibility(b ? View.VISIBLE : View.GONE);
            AAd_num.setVisibility(b ? View.VISIBLE : View.GONE);
            AAd_img.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        @Override
        protected void onAnimationStateChange(int state, boolean willBeSelect) {
            super.onAnimationStateChange(state, willBeSelect);
            if (state == CardStackView.ANIMATION_STATE_START && willBeSelect) {
                onItemExpand(true);
            }
            if (state == CardStackView.ANIMATION_STATE_END && !willBeSelect) {
                onItemExpand(false);
            }
        }

        public void onBind(Integer data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            SharedPreferences sharedpref;
            sharedpref = getContext().getSharedPreferences("MyPref", 0);
            String aad_num = sharedpref.getString("aad_num", "");
            String aad_nam = sharedpref.getString("aad_name", "");
            String aad_addr = sharedpref.getString("aad_address", "");
            String photo = sharedpref.getString("aadhar_img", "");
            byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            AAd_img.setImageBitmap(decodedByte);
            AAd_num.setText(aad_num);
            AAd_name.setText(aad_nam);
            AAd_address.setText(aad_addr);
        }
    }

}