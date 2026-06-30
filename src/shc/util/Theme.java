package shc.util;

import java.awt.*;

public class Theme {

    // Palette — Teal/Navy medical professional theme
    public static final Color PRIMARY       = new Color(14, 116, 144);   // teal-700
    public static final Color PRIMARY_DARK  = new Color(8,  70,  90);   // teal-900
    public static final Color PRIMARY_LIGHT = new Color(207,250,254);   // cyan-100
    public static final Color ACCENT        = new Color(6,  182, 212);  // cyan-500
    public static final Color SUCCESS       = new Color(16, 185, 129);  // emerald-500
    public static final Color DANGER        = new Color(239, 68,  68);  // red-500
    public static final Color WARNING       = new Color(245,158,  11);  // amber-500
    public static final Color INFO          = new Color(59, 130, 246);  // blue-500

    // Sidebar gradient
    public static final Color SIDEBAR_TOP   = new Color(7,  89, 113);
    public static final Color SIDEBAR_BOT   = new Color(3,  46,  61);

    // Backgrounds
    public static final Color BG            = new Color(241,245,249);   // slate-100
    public static final Color CARD          = Color.WHITE;
    public static final Color CARD_ALT      = new Color(248,250,252);   // slate-50
    public static final Color BORDER        = new Color(226,232,240);   // slate-200
    public static final Color BORDER_FOCUS  = new Color(6,  182, 212);

    // Text
    public static final Color TEXT_H        = new Color(15,  23,  42);  // slate-900
    public static final Color TEXT_BODY     = new Color(51,  65,  85);  // slate-700
    public static final Color TEXT_MUTED    = new Color(100,116,139);   // slate-500
    public static final Color TEXT_DISABLED = new Color(148,163,184);   // slate-400

    // Table
    public static final Color TABLE_HEADER  = new Color(241,245,249);
    public static final Color TABLE_STRIPE  = new Color(248,250,252);
    public static final Color TABLE_SELECT  = new Color(207,250,254);

    // Fonts
    public static final Font  FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font  FONT_HEADING  = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font  FONT_SUBHEAD  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font  FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font  FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font  FONT_BADGE    = new Font("Segoe UI", Font.BOLD,  10);
    public static final Font  FONT_MONO     = new Font("Consolas",  Font.PLAIN, 12);
}
