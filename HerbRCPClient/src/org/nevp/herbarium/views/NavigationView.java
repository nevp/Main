package org.nevp.herbarium.views;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class NavigationView extends ViewPart {
	public static final String ID = "HerbRCPClient.navigationView";
	private Gallery gallery;
	

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		gallery = new Gallery(parent,SWT.BORDER | SWT.V_SCROLL | SWT.VIRTUAL);
		gallery.setSize(210, 550);
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setItemSize(190, 225);
		gr.setAutoMargin(true);
		
		gr.setMinMargin(1);
		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		ir.setDropShadows(true);
		ir.setDropShadowsSize(10);

		gallery.setGroupRenderer(gr);
		gallery.setItemRenderer(ir);
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}