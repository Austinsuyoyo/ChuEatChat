package studio.su.austin.chueatchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.eminayar.panter.DialogType;
import com.eminayar.panter.PanterDialog;
import com.eminayar.panter.enums.Animation;
import com.eminayar.panter.interfaces.OnTextInputConfirmListener;

import me.wangyuwei.particleview.ParticleView;

public class FindFriendActivity extends AppCompatActivity {
    private ParticleView mPv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);

        mPv1 = (ParticleView) findViewById(R.id.pv_1);

        mPv1.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                new PanterDialog(FindFriendActivity.this)
                            .setHeaderBackground(R.drawable.pattern_bg_orange)
                            .setHeaderLogo(R.drawable.ic_help)
                            .withAnimation(Animation.POP)
                            .setDialogType(DialogType.INPUT)
                            .isCancelable(false)
                            .input("請問楊方寧到底幾公斤？", new
                                    OnTextInputConfirmListener() {
                                        @Override
                                        public void onTextInputConfirmed(String text) {
                                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                                        }
                                    })
                            .show();
                //Toast.makeText(findfriend.this, "Animation is End", Toast.LENGTH_SHORT).show();
            }
        });

        mPv1.startAnim();
    }
}