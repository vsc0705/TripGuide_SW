package com.example.trip2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class responer_matching_list extends Fragment{

    private responer_matching_list galleryViewModel;

    private static final String TAG = "GalleryFragment";

    String test="kj";
    ListView listview_inter ;
    ListViewAdapter adapter_inter;

    ListView listview_recommend ;
    ListViewAdapter adapter_recommend;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference docRef = db.collection("User").document("zTZDOTmCNIgB5mIsELPX8aHcwaF3");



    //선택 프레그먼트띄우기




    //@@@@@@@@@@@@@
    public static responer_matching_list newInstance(){
        return new responer_matching_list();
    }
    //@@@@@@@@@@@@@@@






    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_responer_matching_list, container, false);







        adapter_inter = new ListViewAdapter() ;
        listview_inter = (ListView) root.findViewById(R.id.inter_list);
        listview_inter.setAdapter(adapter_inter);
        //


        adapter_recommend = new ListViewAdapter() ;
        listview_recommend = (ListView) root.findViewById(R.id.recommend_list);
        listview_recommend.setAdapter(adapter_recommend);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    adapter_inter.addItem(document.get("name").toString(), document.get("user_keyword").toString());
                    adapter_inter.addItem(document.get("name").toString(), document.get("user_keyword").toString());
                    adapter_inter.addItem(document.get("name").toString(), document.get("user_keyword").toString());

                    adapter_inter.notifyDataSetChanged();

                    adapter_recommend.addItem(document.get("name").toString(),document.get("user_keyword").toString());
                    adapter_recommend.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //interested 클릭 리스너
        listview_inter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long test = adapter_inter.getItemId(position);
                String test001 = test.toString();
                Toast.makeText(getActivity(),test001,Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).replaceFragment(fragment_select.newInstance());


            }
        });

        //recommend 클릭 리스너
        listview_recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long test = adapter_recommend.getItemId(position);
                String test001 = test.toString();
                Toast.makeText(getActivity(),test001,Toast.LENGTH_LONG).show();
            }
        });




        //galleryViewModel =
        //        ViewModelProviders.of(this).get(GalleryViewModel.class);




        return root;
    }
}
