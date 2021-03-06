/**********************************************************************
 *
 * Copyright (c) 2017 by Ferenc Hechler
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.gui.ext;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.gui.dialogs.ExportDialog;
import de.willuhn.jameica.hbci.io.Exporter;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Erweitert den Export-Dialog um eine zusaetzliche Option mit der ausgewaehlt
 * werden kann, ob eine Summenzeile ausgegeben werden soll.
 */
public class ExportSVWZExtractorExtension implements Extension
{
  /**
   * Der Context-Schluessel fuer die Option zum Ausblenden des Saldo im Export.
   */
  public final static String KEY_SVWZ_EXTRACT = "svwz.extract";
  
  private final static I18N i18n = Application.getPluginLoader().getPlugin(HBCI.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    if (!(extendable instanceof ExportDialog))
      return;
    
    ExportDialog e = (ExportDialog) extendable;
    
    Class type = e.getType();
    if (!type.isAssignableFrom(Umsatz.class))
      return;
    
    // Erstmal per Default nicht ausblenden
    Exporter.SESSION.put(KEY_SVWZ_EXTRACT,false);
    
    final CheckboxInput check = new CheckboxInput(false);
    check.setName(i18n.tr("Im Verwendungszweck \"SVWZ+\" extrahieren"));
    check.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        Exporter.SESSION.put(KEY_SVWZ_EXTRACT,check.getValue());
      }
    });
    
    final Container c = e.getContainer();
    c.addInput(check);
  }
}


