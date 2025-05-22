package br.com.estudos.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.estudos.myapplication.ui.Cart.Carrinho;
import br.com.estudos.myapplication.ui.Cart.CartFragment;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder> {

    private List<Pizza> pizzaList;
    private Context context;
    private Boolean showAddCart;
    private Boolean showDeleteCart;
    private OnCartUpdateListener cartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public PizzaAdapter(Context context, boolean showAddCart, boolean showDeleteCart, OnCartUpdateListener listener) {
        this.showDeleteCart = showDeleteCart;
        this.context = context;
        this.showAddCart = showAddCart;
        this.pizzaList = new ArrayList<>();
        this.cartUpdateListener = listener;
    }

    public void setPizzaList(List<Pizza> pizzaList) {
        this.pizzaList = pizzaList;
        notifyDataSetChanged();
    }

    @Override
    public PizzaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pizza, parent, false);
        return new PizzaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PizzaViewHolder holder, int position) {
        Pizza pizza = pizzaList.get(position);
        holder.bind(pizza);
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public class PizzaViewHolder extends RecyclerView.ViewHolder {
        private TextView pizzaNameTextView;
        private TextView pizzaPriceTextView;
        private ImageView pizzaImageView;
        private Button addToCartButton;
        private Button deleteCart;

        public PizzaViewHolder(View itemView) {
            super(itemView);
            pizzaNameTextView = itemView.findViewById(R.id.pizzaNameTextView);
            pizzaPriceTextView = itemView.findViewById(R.id.pizzaPriceTextView);
            pizzaImageView = itemView.findViewById(R.id.pizzaImageView);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            deleteCart = itemView.findViewById(R.id.buttonRemoveFromCart);
        }

        public void bind(Pizza pizza) {
            pizzaNameTextView.setText(pizza.getNome());
            pizzaPriceTextView.setText("R$ " + pizza.getPreco());
            Glide.with(pizzaImageView.getContext()).load(pizza.getImagemUrl()).into(pizzaImageView);

            if (showAddCart) {
                addToCartButton.setVisibility(View.VISIBLE);
                addToCartButton.setOnClickListener(v -> {
                    Carrinho.getInstance().adicionarPizza(pizza);
                    Toast.makeText(pizzaImageView.getContext(), "Pizza adicionada ao carrinho", Toast.LENGTH_SHORT).show();
                    if (cartUpdateListener != null) {
                        cartUpdateListener.onCartUpdated();
                    }
                });
            } else {
                addToCartButton.setVisibility(View.GONE);
            }

            if (showDeleteCart) {
                deleteCart.setVisibility(View.VISIBLE);
                deleteCart.setOnClickListener(v -> {
                    Carrinho.getInstance().removerPizza(pizza);
                    Toast.makeText(pizzaImageView.getContext(), "Pizza removida do carrinho", Toast.LENGTH_SHORT).show();
                    if (cartUpdateListener != null) {
                        cartUpdateListener.onCartUpdated();
                    }
                    notifyDataSetChanged();
                });
            } else {
                deleteCart.setVisibility(View.GONE);
            }
        }
    }
}
