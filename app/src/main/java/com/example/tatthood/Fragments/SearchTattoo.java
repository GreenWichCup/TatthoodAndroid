
package com.example.tatthood.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Activity.TestPagerActivity;
import com.example.tatthood.Interfaces.MatchedTattooClickInterface;
import com.example.tatthood.ModelData.Category;
import com.example.tatthood.ModelData.MatchedCategory;
import com.example.tatthood.ModelData.Post;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
import com.example.tatthood.ViewModel.SearchViewModel;
import com.example.tatthood.adapters.MatchedTattooAdapter;
import com.example.tatthood.adapters.SearchTattooAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchTattoo extends Fragment {

    RecyclerView searchTattoo_rv;
    RecyclerView.LayoutManager manager;

    FirebaseDatabase database;
    DatabaseReference referenceTattooPosted,referenceCategory;

    FirebaseRecyclerOptions<Category> optionsSearch;
    FirebaseRecyclerOptions<MatchedCategory> optionsMatched;
    FirebaseRecyclerAdapter<Category, SearchTattooAdapter> adapter_search;
    FirebaseRecyclerAdapter<MatchedCategory, MatchedTattooAdapter> adapter_matched ;
    private List<Post> photoList;


    public SearchTattoo() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        referenceTattooPosted = database.getReference().child("Posts");
        referenceCategory = database.getReference("CategoryMatched");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        manager = new LinearLayoutManager(getContext());
        View view = inflater.inflate(R.layout.fragment_search_tattoo, container, false);
        searchTattoo_rv = view.findViewById(R.id.rv_search_tattoo);
        searchTattoo_rv.setLayoutManager(manager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchViewModel model = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        model.getSelectedStatus().observe(getViewLifecycleOwner(), item -> {
            firebaseSearch(item.toUpperCase());
        });
    }

    private void firebaseSearch(String inputUser){
        Query query = referenceCategory.orderByChild("categoryName_uppercase")
                .startAt(inputUser.toUpperCase()).endAt(inputUser+"\uf8ff");

        optionsSearch = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(query,Category.class)
                .build();

        adapter_search = new FirebaseRecyclerAdapter<Category, SearchTattooAdapter>(optionsSearch) {
            @Override
            protected void onBindViewHolder(@NonNull SearchTattooAdapter searchTattooAdapter, int i, @NonNull Category category) {
                searchTattooAdapter.categoryName.setText(category.getCategoryName());
                Query queryMatch = referenceCategory.child(category.getCategoryName().toLowerCase()).child("matched");
                   optionsMatched = new FirebaseRecyclerOptions.Builder<MatchedCategory>()
                            .setQuery(queryMatch, MatchedCategory.class)
                            .build();
                    adapter_matched = new FirebaseRecyclerAdapter<MatchedCategory, MatchedTattooAdapter>(optionsMatched) {
                        @Override
                        protected void onBindViewHolder(@NonNull MatchedTattooAdapter matchedTattooAdapter, int i, @NonNull MatchedCategory matchedCategory) {
                            GlideApp.with(getContext()).load(matchedCategory.getImageurl()).into(matchedTattooAdapter.matched_image);
                            matchedTattooAdapter.matchedTattooIsClicked(new MatchedTattooClickInterface() {
                                @Override
                                public void onClick(View view, boolean isLongPressed) {
                                    photoList = new ArrayList<>();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(matchedCategory.getPostid());
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           Post post = snapshot.getValue(Post.class);
                                           photoList.add(post);


                                        Intent intent = new Intent(getActivity(), TestPagerActivity.class);
                                            intent.putExtra("index",0);
                                            intent.putExtra("photoList", (Serializable) photoList);
                                            getActivity().startActivity(intent);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                  //  getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,matchedTattooDetail,"imageSelected").commit();

                                }
                            });
                        }

                        @NonNull
                        @Override
                        public MatchedTattooAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view2 = LayoutInflater.from(getContext())
                                    .inflate(R.layout.matched_tattoo_card_item, parent, false);
                            return new MatchedTattooAdapter(view2);
                        }
                    };
                    adapter_matched.startListening();
                    adapter_matched.notifyDataSetChanged();
                    searchTattooAdapter.category_rv.setAdapter(adapter_matched);

                }

            @NonNull
            @Override
            public SearchTattooAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.category_tatoo__card_items,parent,false);
                return new SearchTattooAdapter(view1);
            }
        };
        adapter_search.startListening();
        adapter_search.notifyDataSetChanged();
        searchTattoo_rv.setAdapter(adapter_search);
    }


}
