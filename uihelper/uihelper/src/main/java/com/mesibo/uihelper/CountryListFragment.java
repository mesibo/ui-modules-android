/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-2023 mesibo                                              *
* https://mesibo.com                                                          *
* All rights reserved.                                                        *
*                                                                             *
* Redistribution is not permitted. Use of this software is subject to the     *
* conditions specified at https://mesibo.com . When using the source code,    *
* maintain the copyright notice, conditions, disclaimer, and  links to mesibo * 
* website, documentation and the source code repository.                      *
*                                                                             *
* Do not use the name of mesibo or its contributors to endorse products from  *
* this software without prior written permission.                             *
*                                                                             *
* This software is provided "as is" without warranties. mesibo and its        *
* contributors are not liable for any damages arising from its use.           *
*                                                                             *
* Documentation: https://mesibo.com/documentation/                            *
*                                                                             *
* Source Code Repository: https://github.com/mesibo/                          *
*******************************************************************************/

package com.mesibo.uihelper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

public class CountryListFragment extends BaseDialogFragment {
    //private static final CountryInfo[] countryInfo = {new CountryInfo("Afghanistan", 93), new CountryInfo("Albania", 355), new CountryInfo("Algeria", 213), new CountryInfo("American Samoa", 1684), new CountryInfo("Andorra", 376), new CountryInfo("Angola", 244), new CountryInfo("Anguilla", 1264), new CountryInfo("Antigua", 1268), new CountryInfo("Argentina", 54), new CountryInfo("Armenia", 374), new CountryInfo("Aruba", 297), new CountryInfo("Australia", 61), new CountryInfo("Austria", 43), new CountryInfo("Azerbaijan", 994), new CountryInfo("Bahamas", 1242), new CountryInfo("Bahrain", 973), new CountryInfo("Bangladesh", 880), new CountryInfo("Barbados", 1246), new CountryInfo("Belarus", 375), new CountryInfo("Belgium", 32), new CountryInfo("Belize", 501), new CountryInfo("Benin", 229), new CountryInfo("Bermuda", 1441), new CountryInfo("Bhutan", 975), new CountryInfo("Bolivia", 591), new CountryInfo("Bosnia", 387), new CountryInfo("Botswana", 267), new CountryInfo("Brazil", 55), new CountryInfo("British Virgin Islands", 1284), new CountryInfo("Brunei", 673), new CountryInfo("Bulgaria", 359), new CountryInfo("Burkina Faso", 226), new CountryInfo("Burundi", 257), new CountryInfo("Cambodia", 855), new CountryInfo("Cameroon", 237), new CountryInfo("Canada", 1), new CountryInfo("Cape Verde", 238), new CountryInfo("Cayman Islands", 1345), new CountryInfo("Central African Republic", 236), new CountryInfo("Chad", 235), new CountryInfo("Chile", 56), new CountryInfo("China", 86), new CountryInfo("Colombia", 57), new CountryInfo("Comoros", 269), new CountryInfo("Congo Brazzaville", 242), new CountryInfo("Cook", 682), new CountryInfo("Costa Rica", 506), new CountryInfo("Croatia", 385), new CountryInfo("Cuba", 53), new CountryInfo("Cyprus", 357), new CountryInfo("Czech Republic", 420), new CountryInfo("Denmark", 45), new CountryInfo("Djibouti", 253), new CountryInfo("Dominica", 1767), new CountryInfo("Dominican Republic", 1809), new CountryInfo("Dominican Republic", 1829), new CountryInfo("Dominican Republic", 1849), new CountryInfo("DR of Congo", 243), new CountryInfo("East Timor", 670), new CountryInfo("Ecuador", 593), new CountryInfo("Egypt", 20), new CountryInfo("El Salvador", 503), new CountryInfo("Equatorial Guinea", 240), new CountryInfo("Eritrea", 291), new CountryInfo("Estonia", 372), new CountryInfo("Ethiopia", 251), new CountryInfo("Faeroe", 298), new CountryInfo("Falklands", 500), new CountryInfo("Fiji", 679), new CountryInfo("Finland", 358), new CountryInfo("France", 33), new CountryInfo("French Guiana", 594), new CountryInfo("French Polynesia", 689), new CountryInfo("Gabon", 241), new CountryInfo("Gambia", 220), new CountryInfo("Georgia", 995), new CountryInfo("Germany", 49), new CountryInfo("Ghana", 233), new CountryInfo("Gibraltar", 350), new CountryInfo("Greece", 30), new CountryInfo("Greenland", 299), new CountryInfo("Grenada", 1473), new CountryInfo("Guadeloupe", 590), new CountryInfo("Guam", 1671), new CountryInfo("Guatemala", 502), new CountryInfo("Guinea Bissau", 245), new CountryInfo("Guinea Republic", 224), new CountryInfo("Guyana", 592), new CountryInfo("Haiti", 509), new CountryInfo("Honduras", 504), new CountryInfo("Hong Kong", 852), new CountryInfo("Hungary", 36), new CountryInfo("Iceland", 354), new CountryInfo("India", 91), new CountryInfo("Indonesia", 62), new CountryInfo("Iran", 98), new CountryInfo("Iraq", 964), new CountryInfo("Ireland", 353), new CountryInfo("Israel", 972), new CountryInfo("Italy", 39), new CountryInfo("Ivory Coast", 225), new CountryInfo("Jamaica", 1876), new CountryInfo("Japan", 81), new CountryInfo("Jordan", 962), new CountryInfo("Kazakhstan", 76), new CountryInfo("Kazakhstan", 77), new CountryInfo("Kenya", 254), new CountryInfo("Kiribati", 686), new CountryInfo("Kuwait", 965), new CountryInfo("Kyrghyzstan", 996), new CountryInfo("Laos", 856), new CountryInfo("Latvia", 371), new CountryInfo("Lebanon", 961), new CountryInfo("Lesotho", 266), new CountryInfo("Liberia", 231), new CountryInfo("Libya", 218), new CountryInfo("Liechtenstein", 423), new CountryInfo("Lithuania", 370), new CountryInfo("Luxemburg", 352), new CountryInfo("Macau", 853), new CountryInfo("Macedonia", 389), new CountryInfo("Madagascar", 261), new CountryInfo("Malawi", 265), new CountryInfo("Malaysia", 60), new CountryInfo("Maldives", 960), new CountryInfo("Mali", 223), new CountryInfo("Malta", 356), new CountryInfo("Marshall Islands", 692), new CountryInfo("Martinique", 596), new CountryInfo("Mauritania", 222), new CountryInfo("Mauritius", 230), new CountryInfo("Mexico", 52), new CountryInfo("Micronesia", 691), new CountryInfo("Moldova", 373), new CountryInfo("Monaco", 377), new CountryInfo("Mongolia", 976), new CountryInfo("Montenegro", 382), new CountryInfo("Montserrat", 1664), new CountryInfo("Morocco", 212), new CountryInfo("Mozambique", 258), new CountryInfo("Myanmar", 95), new CountryInfo("Namibia", 264), new CountryInfo("Nauru", 674), new CountryInfo("Nepal", 977), new CountryInfo("Netherlands", 31), new CountryInfo("Netherlands Antillas", 599), new CountryInfo("New Caledonia", 687), new CountryInfo("New Zealand", 64), new CountryInfo("Nicaragua", 505), new CountryInfo("Niger", 227), new CountryInfo("Nigeria", 234), new CountryInfo("North Korea", 850), new CountryInfo("Norway", 47), new CountryInfo("Oman", 968), new CountryInfo("Pakistan", 92), new CountryInfo("Palau", 680), new CountryInfo("Palestine Authority", 970), new CountryInfo("Panama", 507), new CountryInfo("Papua New Guinea", 675), new CountryInfo("Paraguay", 595), new CountryInfo("Peru", 51), new CountryInfo("Philippines", 63), new CountryInfo("Poland", 48), new CountryInfo("Portugal", 351), new CountryInfo("Puerto Rico", 1787), new CountryInfo("Puerto Rico", 1939), new CountryInfo("Qatar", 974), new CountryInfo("Reunion", 262), new CountryInfo("Romania", 40), new CountryInfo("Russia", 7), new CountryInfo("Rwanda", 250), new CountryInfo("Saint Kitts and Nevis", 1869), new CountryInfo("Saint Lucia", 1758), new CountryInfo("Saint Vincent and the Grenadines", 1784), new CountryInfo("San Marino", 378), new CountryInfo("Sao Tome & Principe", 239), new CountryInfo("Saudi Arabia", 966), new CountryInfo("Senegal", 221), new CountryInfo("Serbia", 381), new CountryInfo("Seychelles", 248), new CountryInfo("Sierra Leone", 232), new CountryInfo("Singapore", 65), new CountryInfo("Slovakia", 421), new CountryInfo("Slovenia", 386), new CountryInfo("Solomon Islands", 677), new CountryInfo("Somalia", 252), new CountryInfo("South Africa", 27), new CountryInfo("South Korea", 82), new CountryInfo("Spain", 34), new CountryInfo("Sri Lanka", 94), new CountryInfo("Sudan", 249), new CountryInfo("Suriname", 597), new CountryInfo("Swaziland", 268), new CountryInfo("Sweden", 46), new CountryInfo("Switzerland", 41), new CountryInfo("Syria", 963), new CountryInfo("Taiwan", 886), new CountryInfo("Tajikistan", 992), new CountryInfo("Tanzania", 255), new CountryInfo("Thailand", 66), new CountryInfo("Togo", 228), new CountryInfo("Tonga", 676), new CountryInfo("Trinidad & Tobago", 1868), new CountryInfo("Tunisia", 216), new CountryInfo("Turkey", 90), new CountryInfo("Turkmenistan", 993), new CountryInfo("Turks and Caicos", 1649), new CountryInfo("Uganda", 256), new CountryInfo("Ukraine", 380), new CountryInfo("United Arab Emirates", 971), new CountryInfo("United Kingdom", 44), new CountryInfo("United States", 1), new CountryInfo("Uruguay", 598), new CountryInfo("US Virgin Islands", 1340), new CountryInfo("Uzbekistan", 998), new CountryInfo("Vanuatu", 678), new CountryInfo("Venezuela", 58), new CountryInfo("Vietnam", 84), new CountryInfo("Wallis and Futuna", 681), new CountryInfo("Western Samoa", 685), new CountryInfo("Yemen", 967), new CountryInfo("Zambia", 260), new CountryInfo("Zimbabwe", 263)};

    protected String TAG = "CountryListFragment";
    CountryListerer mListener = null;
    CountryRecyclerViewAdapter mAdapter = null;
    RecyclerView mRecyclerView;
    int mLastPosition = 0;
    boolean mListenerInvoked = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout l  = (LinearLayout) inflater.inflate(R.layout.country_list, container, false);
        mRecyclerView  = (RecyclerView) l.findViewById(R.id.recyclerview);
        setupRecyclerView();
        Dialog d = getDialog();
        if(null != d)
            d.setTitle("Select Country");
        // getDialog().setOnDismissListener(this);
        return l;
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setHasFixedSize(true); 
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        mAdapter = new CountryRecyclerViewAdapter(getActivity(),
                R.layout.country_list_item, R.id.country_name, R.id.country_code);

        mLastPosition = mAdapter.getCountryPosition() - 5; //at fifth position
        if(mLastPosition < 0)
            mLastPosition = 0;

        showCountries();
    }

    private void showCountries() {
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mLastPosition);
    }

    public void setOnCountrySelected(CountryListerer listener) {
        mListener = listener;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss(); // we will not have listener once the fragment is gone, so better we dismiss and let user
        // launch it again
    }

    private void informActivity() {
        if(mListenerInvoked)
            return;

        mListenerInvoked = true;
        mListener.onCountryCanceled();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        informActivity();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        informActivity();
    }

    public class CountryRecyclerViewAdapter extends RecyclerView.Adapter<CountryRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;
        private int mLayoutId, mNameId, mCodeId;
        CountyCodeUtils.CountryInfo[] countryInfo;
        Context mContext;
        int mCountryPosition;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView mNameView;
            public TextView mCodeView;
            public String name, code;
            public int position;

            public ViewHolder(View view, int nameid, int codeid) {
                super(view);
                mView = view;
                mView.setTag(this);
                if(nameid > 0)
                    mNameView = (TextView) view.findViewById(nameid);
                if(codeid > 0)
                    mCodeView = (TextView) view.findViewById(codeid);
            }

        }

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        public CountyCodeUtils.CountryInfo[] getCountries() {
            return CountyCodeUtils.countryInfo;
        }

        public CountryRecyclerViewAdapter(Context context, int layoutid, int nameid, int codeid) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mLayoutId = layoutid;
            mNameId = nameid;
            mCodeId = codeid;
            mContext = context;
            countryInfo = getCountries(); //vhsngr later to be done
            // mCountryPosition = PhoneUtils.getCountryPosition();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayoutId, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view, mNameId, mCodeId);
        }

        public String getCountryCode(int position) {
            return Integer.toString(countryInfo[position].countryCode);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int color = ContextCompat.getColor(getActivity(), R.color.myPrimaryColor);

            if(position == mCountryPosition && mCountryPosition >= 0) {
                holder.mNameView.setTextColor(color);
                if(mCodeId > 0)
                    holder.mCodeView.setTextColor(color);
            } else {
                holder.mNameView.setTextColor(Color.BLACK);
                if(mCodeId > 0)
                    holder.mCodeView.setTextColor(Color.BLACK);
            }

            holder.name = countryInfo[position].countryName;
            holder.code = getCountryCode(position);

            holder.mNameView.setText(countryInfo[position].countryName);
            if(mCodeId > 0)
                holder.mCodeView.setText("+" + getCountryCode(position));

            holder.position = position;

            final int position_f = position;

            //TBD, fix this, keep only one listener
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ViewHolder holder = (ViewHolder) v.getTag();
                    mLastPosition = position_f - 5;
                    if (mLastPosition < 0)
                        mLastPosition = 0;

                    if (null == mListener) {
                        return;
                    }

                    mListenerInvoked = true;
                    mListener.onCountrySelected(holder.name, holder.code);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return countryInfo.length;
        }

        public int getCountryPosition() {
            return mCountryPosition;
        }
    }

    public interface CountryListerer
    {
        //Any number of final, static fields
        //Any number of abstract method declarations\
        void onCountrySelected(String name, String code);
        void onCountryCanceled();

    }
}
