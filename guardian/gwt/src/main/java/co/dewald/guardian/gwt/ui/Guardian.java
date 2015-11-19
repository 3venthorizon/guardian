package co.dewald.guardian.gwt.ui;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.SimpleEventBus;

import com.smartgwt.client.types.BackgroundRepeat;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Dewald Pretorius
 */
public class Guardian implements EntryPoint {

    public static final SimpleEventBus BUS = new SimpleEventBus();

    private static final String P100 = "100%";

    public static VLayout root;
    public static TabSet contentTabSet;
    public static SectionStack contextStack;
    private Layout branding;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        root = new VLayout();
        branding = new Layout();
        Canvas logo = new Canvas();
        HLayout hlayout = new HLayout();
        contextStack = new SectionStack();
        contentTabSet = new TabSet();

        contextStack.setWidth(200);
        contextStack.setShowResizeBar(Boolean.TRUE);
        contextStack.setResizeBarTarget("previous");
        contentTabSet.setSize("*", P100);
        branding.setBackgroundImage("honeycombalpha.png");
        branding.setBackgroundRepeat(BackgroundRepeat.REPEAT);
        branding.setSize(P100, "80px");
        hlayout.addMembers(contextStack, contentTabSet);
        hlayout.setSize(P100, "*");
        logo.setBackgroundImage("warning80.png");
        logo.setBackgroundPosition("right");
        logo.setBackgroundRepeat(BackgroundRepeat.NO_REPEAT);
        logo.setZIndex(Integer.MIN_VALUE);
        logo.setSize(P100, "80px");
        root.setMemberOverlap(22);
        root.addChild(logo);
        root.addMembers(branding, hlayout);
        root.setSize(P100, P100);
        root.draw();
    }
}
