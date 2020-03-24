/*
 *******************************************************************************
 * Copyright (c) 2020 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************
 */
package com.whizzosoftware.osgi.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.concurrent.Callable;

public class Activator implements BundleActivator {
    private ServiceRegistration<Vertx> vertxRegistration;
    private ServiceRegistration<EventBus> eventBusRegistration;

    @Override
    public void start(BundleContext context) throws Exception {
        Vertx vertx = executeWithTCCLSwitch(Vertx::vertx);

        vertxRegistration = context.registerService(Vertx.class, vertx, null);
        eventBusRegistration = context.registerService(EventBus.class, vertx.eventBus(), null);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (vertxRegistration != null) {
            vertxRegistration.unregister();
            vertxRegistration = null;
        }
        if (eventBusRegistration != null) {
            eventBusRegistration.unregister();
            eventBusRegistration = null;
        }
    }

    private <T> T executeWithTCCLSwitch(Callable<T> action) throws Exception {
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(Activator.class.getClassLoader());
            return action.call();
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}
