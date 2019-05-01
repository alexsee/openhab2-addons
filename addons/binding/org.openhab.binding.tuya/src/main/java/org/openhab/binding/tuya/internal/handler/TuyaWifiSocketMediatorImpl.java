// package org.openhab.binding.tuya.internal.handler;
//
// import java.net.SocketException;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Set;
//
// import org.eclipse.smarthome.core.thing.Thing;
// import org.openhab.binding.tuya.internal.TuyaBindingConstants;
// import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketResponse;
// import org.openhab.binding.tuya.runnable.TuyaWifiSocketUpdateReceiverRunnable;
// import org.osgi.service.component.ComponentContext;
// import org.osgi.service.component.annotations.Component;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
/// **
// * The {@link TuyaWifiSocketMediatorImpl} is responsible for receiving all the TCP packets and route correctly to
// * each handler.
// *
// * @author Alexander Seeliger - Initial contribution
// */
// @Component(service = TuyaWifiSocketMediator.class, immediate = true)
// public class TuyaWifiSocketMediatorImpl implements TuyaWifiSocketMediator {
//
// private final Logger logger = LoggerFactory.getLogger(TuyaWifiSocketMediatorImpl.class);
//
// private final Map<Thing, TuyaWifiSocketHandler> handlersRegistredByThing = new HashMap<>();
//
// private TuyaWifiSocketUpdateReceiverRunnable receiver;
// private Thread receiverThread;
//
// /**
// * Called at the service activation.
// *
// * @param componentContext the componentContext
// */
// protected void activate(final ComponentContext componentContext) {
// logger.debug("Mediator has been activated by OSGI.");
// // this.initMediatorWifiSocketUpdateReceiverRunnable();
// }
//
// /**
// * Called at the service deactivation.
// *
// * @param componentContext the componentContext
// */
// protected void deactivate(final ComponentContext componentContext) {
// if (this.receiver != null) {
// this.receiver.shutdown();
// }
// }
//
// /**
// * This method is called by the {@link TuyaWifiSocketUpdateReceiverRunnable}, when one new message has been
// * received.
// *
// * @param receivedMessage the {@link TuyaWifiSocketResponse} message.
// */
// @Override
// public void processReceivedPacket(final String hostAddress, final TuyaWifiSocketResponse receivedMessage) {
// logger.debug("Received packet from: {} with content: [{}]", hostAddress, receivedMessage);
//
// TuyaWifiSocketHandler handler = this.getHandlerRegistredByHost(hostAddress);
//
// if (handler != null) {
// // deliver message to handler.
// handler.newReceivedResponseMessage(receivedMessage);
// logger.debug("Received message delivered with success to handler of host {}", hostAddress);
// }
// }
//
// /**
// * Registers one new {@link Thing} and the corresponding {@link TuyaWifiSocketHandler}.
// *
// * @param thing the {@link Thing}.
// * @param handler the {@link TuyaWifiSocketHandler}.
// */
// @Override
// public void registerThingAndWifiSocketHandler(final Thing thing, final TuyaWifiSocketHandler handler) {
// this.handlersRegistredByThing.put(thing, handler);
// }
//
// /**
// * Unregisters one {@link TuyaWifiSocketHandler} by the corresponding {@link Thing}.
// *
// * @param thing the {@link Thing}.
// */
// @Override
// public void unregisterWifiSocketHandlerByThing(final Thing thing) {
// TuyaWifiSocketHandler handler = this.handlersRegistredByThing.get(thing);
// if (handler != null) {
// this.handlersRegistredByThing.remove(thing);
// }
// }
//
// /**
// * Utility method to get the registered thing handler in mediator by the host address.
// *
// * @param hostAddress the host address of the thing of the handler.
// * @return {@link TuyaWifiSocketHandler} if found.
// */
// private TuyaWifiSocketHandler getHandlerRegistredByHost(final String hostAddress) {
// TuyaWifiSocketHandler searchedHandler = null;
// for (TuyaWifiSocketHandler handler : this.handlersRegistredByThing.values()) {
// if (hostAddress.equals(handler.getHostAddress())) {
// searchedHandler = handler;
// // don't spend more computation. Found the handler.
// break;
// }
// }
// return searchedHandler;
// }
//
// /**
// * Inits the mediator WifiSocketUpdateReceiverRunnable thread. This thread is responsible to receive all
// * packets from Wifi Socket devices, and redirect the messages to mediator.
// */
// private void initMediatorWifiSocketUpdateReceiverRunnable() {
// // try with handler port if is null
// if ((this.receiver == null) || ((this.receiverThread != null)
// && (this.receiverThread.isInterrupted() || !this.receiverThread.isAlive()))) {
// try {
// this.receiver = new TuyaWifiSocketUpdateReceiverRunnable(this,
// TuyaBindingConstants.WIFI_SOCKET_DEFAULT_TCP_PORT);
// this.receiverThread = new Thread(this.receiver);
// this.receiverThread.start();
// logger.debug("Invoked the start of receiver thread.");
// } catch (SocketException e) {
// logger.debug("Cannot start the socket with default port...");
// }
// }
// }
//
// /**
// * Returns all the {@link Thing} registered.
// *
// * @returns all the {@link Thing}.
// */
// @Override
// public Set<Thing> getAllThingsRegistred() {
// return this.handlersRegistredByThing.keySet();
// }
//
// }
