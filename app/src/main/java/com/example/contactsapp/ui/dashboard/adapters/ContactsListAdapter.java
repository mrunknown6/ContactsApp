package com.example.contactsapp.ui.dashboard.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactsapp.R;
import com.example.contactsapp.ui.dashboard.models.ContactModel;

import java.util.ArrayList;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {

    private ContactsListClickListener contactsListClickListener;

    private DiffUtil.ItemCallback<ContactModel> differCallback = new DiffUtil.ItemCallback<ContactModel>() {

        @Override
        public boolean areItemsTheSame(@NonNull ContactModel oldItem, @NonNull ContactModel newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ContactModel oldItem, @NonNull ContactModel newItem) {
            return oldItem.getFirstName().equals(newItem.getFirstName()) &&
                    oldItem.getLastName().equals(newItem.getLastName()) &&
                    oldItem.getPhoneNumber().equals(newItem.getPhoneNumber());
        }
    };

    public AsyncListDiffer<ContactModel> differ = new AsyncListDiffer<ContactModel>(this, differCallback);

    public ContactsListAdapter(ContactsListClickListener contactsListClickListener) {
        this.contactsListClickListener = contactsListClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind();
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFirstNameLastName;
        ContactModel contact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFirstNameLastName = itemView.findViewById(R.id.tvFirstNameLastName);
        }

        public void onBind() {
            contact = differ.getCurrentList().get(getAdapterPosition());
            tvFirstNameLastName.setText(contact.getFirstName().concat(" ").concat(contact.getLastName()));
            itemView.setOnClickListener(v -> contactsListClickListener.onContactClick(getAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                contactsListClickListener.onContactLongClick(getAdapterPosition());
                return true;
            });
        }
    }

}
