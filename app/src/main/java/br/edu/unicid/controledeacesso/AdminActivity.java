package br.edu.unicid.controledeacesso;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import br.edu.unicid.controledeacesso.adapter.AdminPagerAdapter;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout  = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new AdminPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Eventos");        break;
                case 1: tab.setText("Participantes");  break;
                case 2: tab.setText("Dashboard");      break;
                case 3: tab.setText("Solicitações");   break;
                case 4: tab.setText("Relatório");      break;
            }
        }).attach();
    }
}
