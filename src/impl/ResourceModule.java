package impl;

import java.util.LinkedList;
import java.util.Queue;

import handlers.ResquestResourceResponseMessage;
import service.Address;
import service.Payload;
import service.ResourceStatus;

public class ResourceModule {

	private final Queue<Address> RESOURCE_QUEUE = new LinkedList<>();
	private Address actualResourceAddressUser;
	private ApplicationService service;

	public ResourceModule(ApplicationService service) {
		this.service = service;
	}

	public void requestResource(Address address) {
		if(actualResourceAddressUser == null) {
			setActualResourceAddressUser(address);
		} else {
			addToQueue(address);
		}
	}

	public void releaseActualResource() {
		Payload payload = new Payload();
		payload.put("status", ResourceStatus.RELEASED);	
		service.request(actualResourceAddressUser, ResquestResourceResponseMessage.class, payload);
		actualResourceAddressUser = null;
		if(!RESOURCE_QUEUE.isEmpty()) {
			setActualResourceAddressUser(RESOURCE_QUEUE.poll());
		}
	}

	private void addToQueue (Address address) {
		RESOURCE_QUEUE.add(address);
		Payload payload = new Payload();
		payload.put("status", ResourceStatus.IN_QUEUE);
		service.request(address, ResquestResourceResponseMessage.class, payload);
	}
	
	private void setActualResourceAddressUser(Address address) {
		actualResourceAddressUser = address;
		Payload payload = new Payload();
		payload.put("status", ResourceStatus.AVAILABLE);
		service.request(address, ResquestResourceResponseMessage.class, payload);
	}
}
