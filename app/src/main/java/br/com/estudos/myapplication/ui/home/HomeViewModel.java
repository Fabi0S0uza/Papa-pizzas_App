package br.com.estudos.myapplication.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    // LiveData para texto
    private final MutableLiveData<String> mText;

    // LiveData para a navegação
    private final MutableLiveData<Boolean> navigateToLogin;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        navigateToLogin = new MutableLiveData<>(false);
    }

    // Função para obter o texto
    public LiveData<String> getText() {
        return mText;
    }

    // Função para obter o LiveData de navegação
    public LiveData<Boolean> getNavigateToLogin() {
        return navigateToLogin;
    }

    // Função para quando o botão de login for clicado
    public void onLoginButtonClicked() {
        navigateToLogin.setValue(true); // Indica que deve navegar para a tela de login
    }
}
