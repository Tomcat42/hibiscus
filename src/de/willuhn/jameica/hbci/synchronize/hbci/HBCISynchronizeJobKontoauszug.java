/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus/src/de/willuhn/jameica/hbci/server/hbci/synchronize/HBCISynchronizeJobKontoauszug.java,v $
 * $Revision: 1.5 $
 * $Date: 2011/06/30 15:23:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.synchronize.hbci;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import de.willuhn.jameica.hbci.SynchronizeOptions;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.server.hbci.AbstractHBCIJob;
import de.willuhn.jameica.hbci.server.hbci.HBCISaldoJob;
import de.willuhn.jameica.hbci.server.hbci.HBCIUmsatzJob;
import de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJobKontoauszug;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Ein Synchronize-Job fuer das Abrufen der Umsaetze und des Saldos eines Kontos.
 */
public class HBCISynchronizeJobKontoauszug extends AbstractHBCISynchronizeJob implements SynchronizeJobKontoauszug
{
  /**
   * @see de.willuhn.jameica.hbci.synchronize.hbci.HBCISynchronizeJob#createHBCIJobs()
   */
  public AbstractHBCIJob[] createHBCIJobs() throws RemoteException, ApplicationException
  {
    // BUGZILLA 346: Das bleibt weiterhin
    // ein Sync-Job, der aber je nach Konfiguration ggf.
    // nur Saldo oder nur Umsaetze abruft
    Konto k              = (Konto) this.getContext(CTX_ENTITY);
    Boolean forceSaldo   = (Boolean) this.getContext(CTX_FORCE_SALDO);
    Boolean forceUmsatz  = (Boolean) this.getContext(CTX_FORCE_UMSATZ);
    
    SynchronizeOptions o = new SynchronizeOptions(k);
    
    List<AbstractHBCIJob> jobs = new ArrayList<AbstractHBCIJob>();
    if (o.getSyncSaldo() || (forceSaldo != null && forceSaldo.booleanValue())) jobs.add(new HBCISaldoJob(k));
    if (o.getSyncKontoauszuege() || (forceUmsatz != null && forceUmsatz.booleanValue())) jobs.add(new HBCIUmsatzJob(k));

    return jobs.toArray(new AbstractHBCIJob[jobs.size()]);
  }

  /**
   * @see de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJob#getName()
   */
  public String getName() throws ApplicationException
  {
    try
    {
      Konto k = (Konto) this.getContext(CTX_ENTITY);
      SynchronizeOptions o = new SynchronizeOptions(k);
      
      String s = "{0}: ";
      
      if (o.getSyncKontoauszuege())
        s += "Kontoausz�ge";
      if (o.getSyncSaldo())
      {
        if (o.getSyncKontoauszuege())
          s += "/";
        s += "Salden";
      }
      s += " abrufen";
      return i18n.tr(s,k.getLongName());
    }
    catch (RemoteException re)
    {
      Logger.error("unable to determine job name",re);
      throw new ApplicationException(i18n.tr("Auftragsbezeichnung nicht ermittelbar: {0}",re.getMessage()));
    }
  }

  /**
   * @see de.willuhn.jameica.hbci.synchronize.jobs.SynchronizeJob#isRecurring()
   */
  public boolean isRecurring()
  {
    return true;
  }
}