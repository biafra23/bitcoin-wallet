/*
 * Copyright 2014 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import de.schildbach.wallet.WalletApplication;

/**
 * @author Andreas Schildbach
 */
public class PluginServiceImpl extends Service implements PluginService
{
	private WalletApplication application;

	private static final Logger log = LoggerFactory.getLogger(PluginServiceImpl.class);

	@Override
	public void onCreate()
	{
		super.onCreate();

		application = (WalletApplication) getApplication();
	}

	public class LocalBinder extends Binder
	{
		PluginService getService()
		{
			return PluginServiceImpl.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(final Intent intent)
	{
		log.debug(".onBind()");

		return mBinder;
	}

	@Override
	public boolean onUnbind(final Intent intent)
	{
		log.debug(".onUnbind()");

		return super.onUnbind(intent);
	}

	@Override
	public String getAddress()
	{
		return application.determineSelectedAddress().toString();
	}
}
