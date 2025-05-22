package br.com.estudos.myapplication.ui.Cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import br.com.estudos.myapplication.Pizza;

public class Carrinho extends Observable {
    private List<Pizza> pizzas;

    private static Carrinho instance;

    public Carrinho() {
        pizzas = new ArrayList<>();
    }

    public static synchronized Carrinho getInstance() {
        if (instance == null) {
            instance = new Carrinho();
        }
        return instance;
    }

    public void adicionarPizza(Pizza pizza) {
        pizzas.add(pizza);
        setChanged();
        notifyObservers();  // Notify observers when a pizza is added
    }

    public void removerPizza(Pizza pizza) {
        pizzas.remove(pizza);
        setChanged();
        notifyObservers();  // Notify observers when a pizza is removed
    }
    public void esvaziarCarrinho() {
        pizzas.clear(); // Remove todos os itens do carrinho
        setChanged();
        notifyObservers();  // Notifica os observadores sobre a mudança
    }
    public List<Pizza> getPizzas() {
        return pizzas;
    }

    public double calcularCarrinho() {
        double total = 0.0;
        for (Pizza pizza : getPizzas()) {
            total += pizza.getPreco();
        }
        return total;
    }
}
