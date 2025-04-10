package br.com.estudos.myapplication.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.estudos.myapplication.Cupom;
import br.com.estudos.myapplication.CupomAdapter;
import br.com.estudos.myapplication.R;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private CupomAdapter cupomAdapter;
    private List<Cupom> cupomList;
    private FirebaseFirestore db;
    private Spinner spinnerPizzarias;
    private List<String> nomesPizzarias = new ArrayList<>();
    private Map<String, String> pizzariasMap = new HashMap<>(); // Guarda ID -> Nome da pizzaria
    private String pizzariaSelecionadaId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = view.findViewById(R.id.recycler_cupons);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerPizzarias = view.findViewById(R.id.spinner_restaurantes);

        cupomList = new ArrayList<>();
        cupomAdapter = new CupomAdapter(cupomList);
        recyclerView.setAdapter(cupomAdapter);

        db = FirebaseFirestore.getInstance();

        carregarPizzarias();

        return view;
    }

    private void carregarPizzarias() {
        CollectionReference pizzariasRef = db.collection("pizzarias");
        pizzariasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                nomesPizzarias.clear();
                pizzariasMap.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String nome = document.getString("nome");
                    String id = document.getId();

                    pizzariasMap.put(id, nome);
                    nomesPizzarias.add(nome);
                }
                atualizarSpinner();
            }
        });
    }

    private void atualizarSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nomesPizzarias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPizzarias.setAdapter(adapter);

        spinnerPizzarias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nomeSelecionado = nomesPizzarias.get(position);
                pizzariaSelecionadaId = getKeyByValue(pizzariasMap, nomeSelecionado);
                carregarCupons();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void carregarCupons() {
        if (pizzariaSelecionadaId == null) return;

        CollectionReference cuponsRef = db.collection("pizzarias").document(pizzariaSelecionadaId).collection("cupons");
        cuponsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                cupomList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Cupom cupom = doc.toObject(Cupom.class);
                    cupomList.add(cupom);
                }
                cupomAdapter.notifyDataSetChanged();
            }
        });
    }

    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
