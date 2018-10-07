package impl;

import handlers.NotifyResourceAvailableHandler;
import handlers.RequestResourceHandler;
import handlers.ResourceReleasedHandler;
import service.Address;
import service.Payload;
import service.ResourceStatus;

import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ResourceModule {

	private boolean consumingResource;
	private final Queue<Address> queue = new ConcurrentLinkedQueue<>();
	private ApplicationService service;

	public ResourceModule(ApplicationService service) {
		this.service = service;
	}

	public void requestResource() {
		service.getCoordinatorModule().request(RequestResourceHandler.class, new Payload())
			.whenReplied()
			.thenAccept(response -> {
				ResourceStatus status = response.get(PayloadKeys.RESOURCE_STATUS.name());
				if (status.equals(ResourceStatus.OK)) {
					consumeResource();
				}
			});
	}

	public void consumeResource() {
		System.out.println(String.format("Processo de pid %s consumindo recurso.", service.getInfoModule().getPid()));
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println(String.format("Processo de pid %s liberou o recurso.", service.getInfoModule().getPid()));
				if (!queue.isEmpty()) {
					service.getCoordinatorModule().request(ResourceReleasedHandler.class, new Payload());
				}
			}

		}, 5000 + new Random().nextInt(10000));
	}

	/* ----------------- PAPEL DO COORDENADOR  ------------------- */

	public ResourceStatus resourceRequested(Address address) {
		if (consumingResource) {
			queue.add(address);
			return ResourceStatus.IN_QUEUE;
		}
		consumingResource = true;
		return ResourceStatus.OK;
	}

	public void resourceReleased() {
		consumingResource = false;
		if (!queue.isEmpty()) {
			consumingResource = true;
			service.request(queue.poll(), NotifyResourceAvailableHandler.class, new Payload());
		}
	}

}
