package service;

class RequestHandlerExtractor {

    static RequestHandler getRequestHandler(ActionHandler handler) {
        return getRequestHandler(handler.getClass());
    }

    static RequestHandler getRequestHandler(Class<? extends ActionHandler> handler) {
        RequestHandler[] handlerClass = handler.getClass().getDeclaredAnnotationsByType(RequestHandler.class);
        if (handlerClass.length != 1) {
            throw new IllegalArgumentException(String.format("Verifique se a classe '%s' possui a anotação '%s'", handler.getCanonicalName(), RequestHandler.class.getCanonicalName()));
        }
        return handlerClass[0];
    }

}
