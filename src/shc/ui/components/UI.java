package shc.ui.components;

import shc.util.Theme;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

// ── Gradient Panel ────────────────────────────────────────────────────────────
public class UI {

    public static class GradientPanel extends JPanel {
        private final Color c1, c2;
        private final boolean vertical;
        public GradientPanel(Color c1, Color c2, boolean vertical) {
            this.c1=c1; this.c2=c2; this.vertical=vertical; setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(vertical
                ? new GradientPaint(0,0,c1,0,getHeight(),c2)
                : new GradientPaint(0,0,c1,getWidth(),getHeight(),c2));
            g2.fillRect(0,0,getWidth(),getHeight());
            super.paintComponent(g);
        }
    }

    // ── Rounded Card ────────────────────────────────────────────────────────
    public static class Card extends JPanel {
        private final int radius;
        private final Color bg;
        private boolean shadow = true;
        public Card(int radius, Color bg) { this.radius=radius; this.bg=bg; setOpaque(false); }
        public void setShadow(boolean s) { this.shadow=s; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (shadow) {
                g2.setColor(new Color(0,0,0,12));
                g2.fillRoundRect(2,3,getWidth()-3,getHeight()-3,radius,radius);
            }
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,radius,radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Primary Button ───────────────────────────────────────────────────────
    public static class PrimaryBtn extends JButton {
        private final Color base;
        private boolean hovered=false, pressed=false;
        public PrimaryBtn(String text, Color base) {
            super(text); this.base=base;
            setFont(Theme.FONT_SUBHEAD); setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){ hovered=true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered=false; repaint(); }
                public void mousePressed(MouseEvent e){ pressed=true; repaint(); }
                public void mouseReleased(MouseEvent e){ pressed=false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = pressed ? base.darker() : (hovered ? base.brighter() : base);
            g2.setColor(c);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
            g2.setColor(new Color(255,255,255,35));
            g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Ghost/Outline Button ─────────────────────────────────────────────────
    public static class OutlineBtn extends JButton {
        private final Color accent;
        private boolean hovered=false;
        public OutlineBtn(String text, Color accent) {
            super(text); this.accent=accent;
            setFont(Theme.FONT_BODY); setForeground(accent);
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){ hovered=true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered=false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (hovered) { g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),20)); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10); }
            g2.setColor(accent); g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,10,10);
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ── Labeled Text Field ───────────────────────────────────────────────────
    public static class Field extends JTextField {
        private String placeholder;
        public Field(String ph) {
            placeholder=ph;
            setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_H);
            setBackground(Theme.CARD_ALT);
            setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Theme.BORDER, 8),
                BorderFactory.createEmptyBorder(9,12,9,12)));
            setOpaque(true);
            addFocusListener(new FocusAdapter(){
                public void focusGained(FocusEvent e){
                    setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(Theme.BORDER_FOCUS, 8),
                        BorderFactory.createEmptyBorder(9,12,9,12)));
                    repaint();
                }
                public void focusLost(FocusEvent e){
                    setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(Theme.BORDER, 8),
                        BorderFactory.createEmptyBorder(9,12,9,12)));
                    repaint();
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()&&!isFocusOwner()&&placeholder!=null) {
                Graphics2D g2=(Graphics2D)g;
                g2.setColor(Theme.TEXT_DISABLED);
                g2.setFont(new Font("Segoe UI",Font.ITALIC,12));
                Insets ins=getInsets();
                g2.drawString(placeholder, ins.left, getHeight()-ins.bottom-5);
            }
        }
    }

    // ── Password Field ───────────────────────────────────────────────────────
    public static class PassField extends JPasswordField {
        public PassField() {
            setFont(Theme.FONT_BODY); setForeground(Theme.TEXT_H);
            setBackground(Theme.CARD_ALT); setEchoChar('●');
            setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Theme.BORDER, 8),
                BorderFactory.createEmptyBorder(9,12,9,12)));
            setOpaque(true);
            addFocusListener(new FocusAdapter(){
                public void focusGained(FocusEvent e){
                    setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(Theme.BORDER_FOCUS, 8),
                        BorderFactory.createEmptyBorder(9,12,9,12))); repaint();
                }
                public void focusLost(FocusEvent e){
                    setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(Theme.BORDER, 8),
                        BorderFactory.createEmptyBorder(9,12,9,12))); repaint();
                }
            });
        }
    }

    // ── Rounded Border ───────────────────────────────────────────────────────
    public static class RoundedLineBorder extends AbstractBorder {
        private final Color color; private final int r;
        public RoundedLineBorder(Color c, int r){ color=c; this.r=r; }
        @Override public void paintBorder(Component c,Graphics g,int x,int y,int w,int h){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.drawRoundRect(x,y,w-1,h-1,r,r); g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c){ return new Insets(r/2,r/2,r/2,r/2); }
        @Override public Insets getBorderInsets(Component c,Insets in){ in.set(r/2,r/2,r/2,r/2); return in; }
    }

    // ── Stat Card ────────────────────────────────────────────────────────────
    public static class StatCard extends JPanel {
        private JLabel numLabel, titleLabel, changeLabel;
        public StatCard(String title, String value, String change, Color accent) {
            setOpaque(false);
            Card card=new Card(14, Color.WHITE);
            card.setLayout(new BorderLayout(0,6));
            card.setBorder(BorderFactory.createEmptyBorder(18,20,18,20));

            // Color strip on left
            JPanel strip=new JPanel();
            strip.setBackground(accent); strip.setPreferredSize(new Dimension(4,0));
            strip.setOpaque(true);

            numLabel=new JLabel(value);
            numLabel.setFont(new Font("Segoe UI",Font.BOLD,28));
            numLabel.setForeground(Theme.TEXT_H);

            titleLabel=new JLabel(title);
            titleLabel.setFont(Theme.FONT_SMALL);
            titleLabel.setForeground(Theme.TEXT_MUTED);

            changeLabel=new JLabel(change);
            changeLabel.setFont(Theme.FONT_BADGE);
            changeLabel.setForeground(accent);

            JPanel textBlock=new JPanel(new GridLayout(3,1,0,2)); textBlock.setOpaque(false);
            textBlock.add(titleLabel); textBlock.add(numLabel); textBlock.add(changeLabel);

            card.add(strip,BorderLayout.WEST);
            card.add(textBlock,BorderLayout.CENTER);

            setLayout(new BorderLayout());
            add(card);
        }
        public void setValue(String v){ numLabel.setText(v); }
        public void setChange(String v){ changeLabel.setText(v); }
    }

    // ── Status Badge ─────────────────────────────────────────────────────────
    public static JLabel badge(String text, Color fg, Color bg) {
        JLabel l=new JLabel(text,SwingConstants.CENTER){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.dispose(); super.paintComponent(g);
            }
        };
        l.setFont(Theme.FONT_BADGE); l.setForeground(fg); l.setBackground(bg);
        l.setOpaque(false); l.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
        return l;
    }

    // ── Section Label ────────────────────────────────────────────────────────
    public static JLabel sectionLabel(String text) {
        JLabel l=new JLabel(text.toUpperCase());
        l.setFont(new Font("Segoe UI",Font.BOLD,10));
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    // ── Heading + Subtitle ───────────────────────────────────────────────────
    public static JLabel pageTitle(String title, String sub) {
        return new JLabel("<html>"
            +"<span style='font-size:17px;font-weight:bold;color:#0F172A'>"+title+"</span>"
            +"<br><span style='font-size:11px;color:#64748B'>"+sub+"</span></html>");
    }

    // ── Styled JTable ────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(Theme.FONT_BODY);
        table.setBackground(Color.WHITE);
        table.setGridColor(Theme.BORDER);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(Theme.TABLE_SELECT);
        table.setSelectionForeground(Theme.TEXT_H);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setFocusable(false);

        JTableHeader hdr=table.getTableHeader();
        hdr.setFont(Theme.FONT_SUBHEAD);
        hdr.setBackground(Theme.TABLE_HEADER);
        hdr.setForeground(Theme.TEXT_MUTED);
        hdr.setPreferredSize(new Dimension(0,40));
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Theme.BORDER));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if (!sel) {
                    setBackground(row%2==0 ? Color.WHITE : Theme.TABLE_STRIPE);
                    setForeground(Theme.TEXT_BODY);
                }
                setBorder(BorderFactory.createEmptyBorder(0,14,0,14));
                return this;
            }
        });
    }

    // ── Scroll Pane ─────────────────────────────────────────────────────────
    public static JScrollPane scrollPane(Component view) {
        JScrollPane sp=new JScrollPane(view);
        sp.setBorder(new RoundedLineBorder(Theme.BORDER, 10));
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBackground(Color.WHITE);
        return sp;
    }

    // ── Form Row ─────────────────────────────────────────────────────────────
    public static JPanel formRow(String label, JComponent field) {
        JPanel p=new JPanel(new BorderLayout(0,5)); p.setOpaque(false);
        JLabel lbl=new JLabel(label); lbl.setFont(Theme.FONT_SUBHEAD); lbl.setForeground(Theme.TEXT_BODY);
        p.add(lbl,BorderLayout.NORTH); p.add(field,BorderLayout.CENTER);
        return p;
    }

    // ── Status Message ───────────────────────────────────────────────────────
    public static JLabel statusLabel() {
        JLabel l=new JLabel(" ");
        l.setFont(Theme.FONT_BODY);
        return l;
    }

    public static void setStatus(JLabel lbl, String msg, boolean success) {
        lbl.setText("  " + msg);
        lbl.setForeground(success ? Theme.SUCCESS : Theme.DANGER);
    }

    public static void setStatusWarn(JLabel lbl, String msg) {
        lbl.setText("  " + msg);
        lbl.setForeground(Theme.WARNING);
    }
}
