// package org.openhab.binding.tuya.internal.handler;
//
// import java.util.Set;
//
// import org.eclipse.smarthome.core.thing.Thing;
// import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketResponse;
// import org.openhab.binding.tuya.runnable.TuyaWifiSocketUpdateReceiverRunnable;
//
/// **
// * The {@link TuyaWifiSocketMediator} is responsible for receiving all the UDP packets and route correctly to
// * each handler.
// *
// * @author Alexander Seeliger - Initial contribution
// */
// public interface TuyaWifiSocketMediator {
//
// /**
// * This method is called by the {@link TuyaWifiSocketUpdateReceiverRunnable}, when one new message has been
// * received.
// *
// * @param receivedMessage the {@link TuyaWifiSocketResponse} message.
// */
// void processReceivedPacket(final String hostAddress, final TuyaWifiSocketResponse receivedMessage);
//
// /**
// * Registers a new {@link Thing} and the corresponding {@link TuyaWifiSocketHandler}.
// *
// * @param thing the {@link Thing}.
// * @param handler the {@link TuyaWifiSocketHandler}.
// */
// void registerThingAndWifiSocketHandler(final Thing thing, final TuyaWifiSocketHandler handler);
//
// /**
// * Unregisters a {@link TuyaWifiSocketHandler} by the corresponding {@link Thing}.
// *
// * @param thing the {@link Thing}.
// */
// void unregisterWifiSocketHandlerByThing(final Thing thing);
//
// /**
// * Returns all the {@link Thing} registered.
// *
// * @returns all the {@link Thing}.
// */
// Set<Thing> getAllThingsRegistred();
//
// }
