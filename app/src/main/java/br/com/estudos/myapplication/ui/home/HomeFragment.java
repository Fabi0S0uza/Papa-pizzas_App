package br.com.estudos.myapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import br.com.estudos.myapplication.Login;
import br.com.estudos.myapplication.PizzaAdapter;
import br.com.estudos.myapplication.R;
import br.com.estudos.myapplication.databinding.FragmentHomeBinding;
import br.com.estudos.myapplication.Pizza;
import br.com.estudos.myapplication.ui.Cart.CartFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private PizzaAdapter adapter;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerViewPizzas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PizzaAdapter(getContext(), true, false, this::atualizarCarrinho);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("pizzas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Pizza> pizzas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pizza pizza = document.toObject(Pizza.class);
                            pizzas.add(pizza);
                        }
                        adapter.setPizzaList(pizzas);
                    }
                });

        // Acessando a ImageView do logo
        ImageView logo = root.findViewById(R.id.logo);
        logo.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        });

        return root;
    }

    private void atualizarCarrinho() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        CartFragment cartFragment = (CartFragment) fragmentManager.findFragmentByTag("cart_fragment");

        if (cartFragment != null) {
            cartFragment.atualizarTotal();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
