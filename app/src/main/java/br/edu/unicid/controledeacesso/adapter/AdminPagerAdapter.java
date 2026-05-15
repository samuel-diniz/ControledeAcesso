package br.edu.unicid.controledeacesso.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import br.edu.unicid.controledeacesso.fragment.DashboardFragment;
import br.edu.unicid.controledeacesso.fragment.EventoFragment;
import br.edu.unicid.controledeacesso.fragment.ParticipanteFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:  return new ParticipanteFragment();
            case 2:  return new DashboardFragment();
            default: return new EventoFragment();
        }
    }

    @Override public int getItemCount() { return 3; }
}
