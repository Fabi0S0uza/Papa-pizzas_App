package br.com.estudos.myapplication.ui.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import br.com.estudos.myapplication.Login;
import br.com.estudos.myapplication.R;
import br.com.estudos.myapplication.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private Switch switchDarkMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        binding.btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(getContext(), "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();

            // Redireciona para a tela de login
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Shared Preferences para salvar o estado do modo escuro
        sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("darkMode", false);
        binding.switchDarkMode.setChecked(isDarkMode);

        // Aplica o modo escuro
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Salva a preferência do usuário
            sharedPreferences.edit().putBoolean("darkMode", isChecked).apply();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Libera a referência da view
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Não há recursos específicos que pareçam precisar ser liberados neste Fragment no momento.
    }
}