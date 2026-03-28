package com.example.tictactoe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    // ── Views ──────────────────────────────────────────────────────────────────
    private TextView tvStatus, tvScoreX, tvScoreO, tvScoreDraw, tvTitle;
    private Button btnReset, btnResetScores;
    private Button[][] cellButtons = new Button[3][3];

    // ── Game ───────────────────────────────────────────────────────────────────
    private TicTacToe game;

    // ── Colors ─────────────────────────────────────────────────────────────────
    private int colorCyan, colorPink, colorYellow, colorDark, colorCardBg, colorWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initColors();
        bindViews();
        game = new TicTacToe();
        setupCellButtons();
        setupControlButtons();
        animateIntro();
        updateStatus();
    }

    // ── Setup ──────────────────────────────────────────────────────────────────

    private void initColors() {
        colorCyan   = ContextCompat.getColor(this, R.color.neon_cyan);
        colorPink   = ContextCompat.getColor(this, R.color.neon_pink);
        colorYellow = ContextCompat.getColor(this, R.color.neon_yellow);
        colorDark   = ContextCompat.getColor(this, R.color.bg_dark);
        colorCardBg = ContextCompat.getColor(this, R.color.card_bg);
        colorWhite  = ContextCompat.getColor(this, R.color.white);
    }

    private void bindViews() {
        tvTitle      = findViewById(R.id.tvTitle);
        tvStatus     = findViewById(R.id.tvStatus);
        tvScoreX     = findViewById(R.id.tvScoreX);
        tvScoreO     = findViewById(R.id.tvScoreO);
        tvScoreDraw  = findViewById(R.id.tvScoreDraw);
        btnReset     = findViewById(R.id.btnReset);
        btnResetScores = findViewById(R.id.btnResetScores);

        int[][] ids = {
                {R.id.btn00, R.id.btn01, R.id.btn02},
                {R.id.btn10, R.id.btn11, R.id.btn12},
                {R.id.btn20, R.id.btn21, R.id.btn22}
        };
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                cellButtons[r][c] = findViewById(ids[r][c]);
    }

    private void setupCellButtons() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                final int row = r, col = c;
                cellButtons[r][c].setOnClickListener(v -> onCellClicked(row, col));
            }
        }
    }

    private void setupControlButtons() {
        btnReset.setOnClickListener(v -> {
            animateButtonPress(btnReset);
            resetGame();
        });
        btnResetScores.setOnClickListener(v -> {
            animateButtonPress(btnResetScores);
            game.resetAll();
            resetGame();
            updateScores();
            Toast.makeText(this, "Scores reset!", Toast.LENGTH_SHORT).show();
        });
    }

    // ── Game Logic ─────────────────────────────────────────────────────────────

    private void onCellClicked(int row, int col) {
        if (game.isGameOver()) return;

        boolean moved = game.makeMove(row, col);
        if (!moved) {
            // Invalid move — shake the cell
            animateShake(cellButtons[row][col]);
            return;
        }

        // Animate the mark appearing
        char val = game.getCellValue(row, col);
        Button btn = cellButtons[row][col];
        btn.setText(String.valueOf(val));
        btn.setEnabled(false);
        int markColor = (val == TicTacToe.PLAYER_X) ? colorCyan : colorPink;
        btn.setTextColor(markColor);
        animateCellMark(btn);

        updateStatus();
        updateScores();

        TicTacToe.GameState state = game.getGameState();
        if (state == TicTacToe.GameState.X_WINS || state == TicTacToe.GameState.O_WINS) {
            highlightWinnerCells();
            animateWinTitle(state);
        } else if (state == TicTacToe.GameState.DRAW) {
            animateDrawPulse();
        }
    }

    private void resetGame() {
        game.resetBoard();

        // Fade out all cells, then clear
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                final Button btn = cellButtons[r][c];
                final int delay = (r * 3 + c) * 40;
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    btn.animate().alpha(0f).setDuration(150).withEndAction(() -> {
                        btn.setText("");
                        btn.setEnabled(true);
                        btn.setBackgroundTintList(
                                ContextCompat.getColorStateList(this, R.color.card_bg));
                        btn.animate().alpha(1f).setDuration(200).start();
                    }).start();
                }, delay);
            }
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::updateStatus, 500);
    }

    private void updateStatus() {
        TicTacToe.GameState state = game.getGameState();
        String msg;
        int color;
        switch (state) {
            case X_WINS:
                msg = "🏆 Player X Wins!";
                color = colorCyan;
                break;
            case O_WINS:
                msg = "🏆 Player O Wins!";
                color = colorPink;
                break;
            case DRAW:
                msg = "⚡ It's a Draw!";
                color = colorYellow;
                break;
            default:
                char p = game.getCurrentPlayer();
                msg = "Player " + p + "'s Turn";
                color = (p == TicTacToe.PLAYER_X) ? colorCyan : colorPink;
        }
        tvStatus.setText(msg);
        tvStatus.setTextColor(color);
    }

    private void updateScores() {
        animateCountUp(tvScoreX, game.getScoreX());
        animateCountUp(tvScoreO, game.getScoreO());
        animateCountUp(tvScoreDraw, game.getScoreDraw());
    }

    // ── Animations ─────────────────────────────────────────────────────────────

    /** Staggered entrance animation for the whole UI. */
    private void animateIntro() {
        tvTitle.setAlpha(0f);
        tvTitle.setTranslationY(-60f);
        tvTitle.animate().alpha(1f).translationY(0f)
                .setDuration(600).setInterpolator(new OvershootInterpolator()).start();

        // Cascade cells
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button btn = cellButtons[r][c];
                btn.setAlpha(0f);
                btn.setScaleX(0.5f);
                btn.setScaleY(0.5f);
                int delay = 300 + (r * 3 + c) * 60;
                btn.animate().alpha(1f).scaleX(1f).scaleY(1f)
                        .setStartDelay(delay).setDuration(350)
                        .setInterpolator(new OvershootInterpolator(1.5f)).start();
            }
        }
    }

    /** Pop + scale animation when a mark is placed. */
    private void animateCellMark(Button btn) {
        btn.setScaleX(0f);
        btn.setScaleY(0f);
        btn.animate().scaleX(1f).scaleY(1f)
                .setDuration(350)
                .setInterpolator(new OvershootInterpolator(2f))
                .start();
    }

    /** Horizontal shake for invalid moves. */
    private void animateShake(View v) {
        ObjectAnimator shaker = ObjectAnimator.ofFloat(v, "translationX",
                0f, -18f, 18f, -14f, 14f, -8f, 8f, 0f);
        shaker.setDuration(400);
        shaker.setInterpolator(new AccelerateDecelerateInterpolator());
        shaker.start();
    }

    /** Bounce press effect on buttons. */
    private void animateButtonPress(View v) {
        v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(80)
                .withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).setDuration(200)
                                .setInterpolator(new BounceInterpolator()).start()
                ).start();
    }

    /** Flash + color-shift the winning cells. */
    private void highlightWinnerCells() {
        int[] winning = game.getWinningCells();
        if (winning == null) return;

        for (int flat : winning) {
            int r = flat / 3, c = flat % 3;
            Button btn = cellButtons[r][c];

            // Pulse scale
            btn.animate().scaleX(1.15f).scaleY(1.15f).setDuration(200)
                    .withEndAction(() ->
                            btn.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                    ).start();

            // Color flash on background
            int winColor = (game.getCellValue(r, c) == TicTacToe.PLAYER_X) ? colorCyan : colorPink;
            ValueAnimator colorAnim = ValueAnimator.ofObject(
                    new ArgbEvaluator(), colorCardBg, winColor, colorCardBg);
            colorAnim.setDuration(800);
            colorAnim.setRepeatCount(3);
            colorAnim.addUpdateListener(anim ->
                    btn.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf((int) anim.getAnimatedValue()))
            );
            colorAnim.start();
        }
    }

    /** Win announcement: status pulsates. */
    private void animateWinTitle(TicTacToe.GameState state) {
        int targetColor = (state == TicTacToe.GameState.X_WINS) ? colorCyan : colorPink;
        ValueAnimator pulser = ValueAnimator.ofFloat(1f, 1.3f, 1f);
        pulser.setDuration(600);
        pulser.setRepeatCount(3);
        pulser.addUpdateListener(anim -> {
            float s = (float) anim.getAnimatedValue();
            tvStatus.setScaleX(s);
            tvStatus.setScaleY(s);
        });
        pulser.start();
    }

    /** Yellow pulse on draw. */
    private void animateDrawPulse() {
        ValueAnimator colorAnim = ValueAnimator.ofObject(
                new ArgbEvaluator(), colorWhite, colorYellow, colorWhite);
        colorAnim.setDuration(500);
        colorAnim.setRepeatCount(3);
        colorAnim.addUpdateListener(anim ->
                tvStatus.setTextColor((int) anim.getAnimatedValue())
        );
        colorAnim.start();
    }

    /** Animated count-up for score TextViews. */
    private void animateCountUp(TextView tv, int target) {
        int current;
        try { current = Integer.parseInt(tv.getText().toString()); }
        catch (NumberFormatException e) { current = 0; }

        if (current == target) return;

        ValueAnimator anim = ValueAnimator.ofInt(current, target);
        anim.setDuration(400);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(a -> tv.setText(String.valueOf((int) a.getAnimatedValue())));

        // Pop the score view
        tv.animate().scaleX(1.4f).scaleY(1.4f).setDuration(150)
                .withEndAction(() ->
                        tv.animate().scaleX(1f).scaleY(1f).setDuration(200)
                                .setInterpolator(new BounceInterpolator()).start()
                ).start();
        anim.start();
    }
}