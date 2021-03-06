package com.example.contactsapp.ui.dashboard.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.contactsapp.R;
import com.example.contactsapp.db.Image;
import com.example.contactsapp.db.ImageDatabase;
import com.example.contactsapp.ui.authentication.viewmodel.DashboardViewModelFactory;
import com.example.contactsapp.ui.dashboard.adapters.ContactsListAdapter;
import com.example.contactsapp.ui.dashboard.adapters.ContactsListClickListener;
import com.example.contactsapp.ui.dashboard.models.ContactModel;
import com.example.contactsapp.ui.dashboard.viewmodel.DashboardViewModel;
import com.example.contactsapp.ui.modify_contact.ModifyContactActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.atomic.AtomicReference;

import static android.app.Activity.RESULT_OK;


public class ContactsListFragment extends Fragment {

    private ContactsListAdapter adapter;
    private RecyclerView rvContactsList;

    private FloatingActionButton fab;

    private DashboardViewModel viewModel;

    // position to modify the ArrayList upon return from modification activity
    private int position = -1;

    private static int REQUEST_CODE_UPDATE = 1003;
    private static int REQUEST_CODE_ADD = 1004;

    public ContactsListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // instantiate view model
        DashboardViewModelFactory factory = new DashboardViewModelFactory(getActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(DashboardViewModel.class);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            // open add activity
            Intent intent = new Intent(getActivity(), ModifyContactActivity.class);
            intent.putExtra("operation", "add");
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });

        // setup
        setupRecyclerView(view);
        setupObservers();

        // fetch contacts
        viewModel.fetchContacts();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // if we updated a contact
            if (requestCode == REQUEST_CODE_UPDATE) {
                ContactModel contactModel = data.getExtras().getParcelable("contact");
                if (contactModel != null) {
                    Toast.makeText(getContext(), contactModel.toString(), Toast.LENGTH_SHORT).show();
                    viewModel.getContacts().getValue().set(position, contactModel);
                    adapter.differ.submitList(viewModel.getContacts().getValue());
                    adapter.notifyItemChanged(position);
                }
            }
            
            // if we added a new contact
            else if (requestCode == REQUEST_CODE_ADD) {
                String phoneNumber = data.getExtras().getString("phone_number");
                viewModel.fetchContacts();

                // this doesn't work for some reason...
                for (int i = 0; i < adapter.differ.getCurrentList().size(); i++) {
                    if (phoneNumber.equals(adapter.differ.getCurrentList().get(i).getPhoneNumber())) {
                        rvContactsList.scrollToPosition(i);
                        Toast.makeText(getContext(), "HERE", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }

    private void setupRecyclerView(View view) {
        rvContactsList = view.findViewById(R.id.rvContactsList);
        rvContactsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvContactsList.setHasFixedSize(true);
        adapter = new ContactsListAdapter(new ContactsListClickListener() {
            @Override
            public void onContactClick(int adapterPosition) {
                Intent intent = new Intent(getActivity(), ModifyContactActivity.class);
                intent.putExtra("operation", "update");
                intent.putExtra("contact", viewModel.getContacts().getValue().get(adapterPosition));
                position = adapterPosition;
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            }

            @Override
            public void onContactLongClick(int adapterPosition) {
                ContactModel contactModel = adapter.differ.getCurrentList().get(adapterPosition);
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete contact?")
                        .setMessage("Are you sure you want to delete this contact?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            viewModel.deleteContact(getActivity().getContentResolver(), contactModel);
                            adapter.notifyItemRemoved(adapterPosition);
                            dialog.cancel();
                            Toast.makeText(getContext(), "WTF", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.cancel();
                        }).show();
            }
        }
        );
        rvContactsList.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getContacts().observe(getViewLifecycleOwner(), contactModels -> {
            adapter.differ.submitList(contactModels);
        });
    }
}