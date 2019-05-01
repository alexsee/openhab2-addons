package org.openhab.binding.tuya.internal;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.tuya.internal.handler.TuyaWifiSocketHandler;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TuyaHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Alexander Seeliger - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.tuya")
public class TuyaHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .singleton(TuyaBindingConstants.THING_TYPE_WIFI_SOCKET);

    private final Logger logger = LoggerFactory.getLogger(TuyaHandlerFactory.class);

    // private TuyaWifiSocketMediator mediator;
    //
    // /**
    // * Used by OSGI to inject the mediator in the handler factory.
    // *
    // * @param mediator the mediator
    // */
    // @Reference
    // public void setMediator(final TuyaWifiSocketMediator mediator) {
    // logger.debug("Mediator has been injected on handler factory service.");
    // this.mediator = mediator;
    // }
    //
    // /**
    // * Used by OSGI to unsets the mediator from the handler factory.
    // *
    // * @param mediator the mediator
    // */
    // public void unsetMediator(final TuyaWifiSocketMediator mitsubishiMediator) {
    // logger.debug("Mediator has been unsetted from discovery service.");
    // this.mediator = null;
    // }

    @Override
    public boolean supportsThingType(final ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(final Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(TuyaBindingConstants.THING_TYPE_WIFI_SOCKET)) {
            TuyaWifiSocketHandler handler;
            logger.debug("Creating a new TuyaWifiSocketHandler...");

            handler = new TuyaWifiSocketHandler(thing);
            logger.debug("TuyaWifiSocketMediator will register the handler.");
            // if (this.mediator != null) {
            // this.mediator.registerThingAndWifiSocketHandler(thing, handler);
            // } else {
            // logger.error(
            // "The mediator is missing on Handler factory. Without one mediator the handler cannot work!");
            // return null;
            // }
            return handler;
        }
        return null;
    }

    // @Override
    // public void unregisterHandler(final Thing thing) {
    // if (this.mediator != null) {
    // this.mediator.unregisterWifiSocketHandlerByThing(thing);
    // }
    // super.unregisterHandler(thing);
    // }

}
