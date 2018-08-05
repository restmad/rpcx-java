package com.colobu.rpcx.rpc;


import com.colobu.rpcx.common.NetUtils;
import com.colobu.rpcx.config.Constants;
import com.colobu.rpcx.netty.ResponseFuture;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;


/**
 * @author goodjava@qq.com
 */
public class RpcContext {
	
	private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
		@Override
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};

	/**
	 * get context.
	 * 
	 * @return context
	 */
	public static RpcContext getContext() {
	    return LOCAL.get();
	}
	
	/**
	 * remove context.
	 */
	public static void removeContext() {
	    LOCAL.remove();
	}

    private ResponseFuture future;

    private List<URL> urls;

    private URL url;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

	private InetSocketAddress localAddress;

	private InetSocketAddress remoteAddress;

    private final Map<String, String> attachments = new HashMap<String, String>();

    private final Map<String, Object> values = new HashMap<String, Object>();

    private String serviceAddr;

	protected RpcContext() {
	}

    /**
     * is provider side.
     * 
     * @return provider side.
     */
    public boolean isProviderSide() {
        URL url = getUrl();
        if (url == null) {
            return false;
        }
        InetSocketAddress address = getRemoteAddress();
        if (address == null) {
            return false;
        }
        String host;
        if (address.getAddress() == null) {
            host = address.getHostName();
        } else {
            host = address.getAddress().getHostAddress();
        }
        return url.getPort() != address.getPort() || 
                ! NetUtils.filterLocalHost(url.getIp()).equals(NetUtils.filterLocalHost(host));
    }

    /**
     * is consumer side.
     * 
     * @return consumer side.
     */
    public boolean isConsumerSide() {
        URL url = getUrl();
        if (url == null) {
            return false;
        }
        InetSocketAddress address = getRemoteAddress();
        if (address == null) {
            return false;
        }
        String host;
        if (address.getAddress() == null) {
            host = address.getHostName();
        } else {
            host = address.getAddress().getHostAddress();
        }
        return url.getPort() == address.getPort() && 
                NetUtils.filterLocalHost(url.getIp()).equals(NetUtils.filterLocalHost(host));
    }

    /**
     * get future.
     * @return future
     */
    public ResponseFuture getFuture() {
        return this.future;
    }

    /**
     * set future.
     * 
     * @param future
     */
    public void setFuture(ResponseFuture future) {
        this.future = future;
    }

    public List<URL> getUrls() {
        return urls == null && url != null ? (List<URL>) Arrays.asList(url) : urls;
    }

    public void setUrls(List<URL> urls) {
        this.urls = urls;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * get method name.
     * 
     * @return method name.
     */
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * get parameter types.
     * 
     * @serial
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * get arguments.
     * 
     * @return arguments.
     */
    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    /**
     * set local address.
     * 
     * @param address
     * @return context
     */
	public RpcContext setLocalAddress(InetSocketAddress address) {
	    this.localAddress = address;
	    return this;
	}

	/**
	 * set local address.
	 * 
	 * @param host
	 * @param port
	 * @return context
	 */
    public RpcContext setLocalAddress(String host, int port) {
        if (port < 0) {
            port = 0;
        }
        this.localAddress = InetSocketAddress.createUnresolved(host, port);
        return this;
    }

	/**
	 * get local address.
	 * 
	 * @return local address
	 */
	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	public String getLocalAddressString() {
        return getLocalHost() + ":" + getLocalPort();
    }
    
	/**
	 * get local host name.
	 * 
	 * @return local host name
	 */
	public String getLocalHostName() {
		String host = localAddress == null ? null : localAddress.getHostName();
		if (host == null || host.length() == 0) {
		    return getLocalHost();
		}
		return host;
	}

    /**
     * set remote address.
     * 
     * @param address
     * @return context
     */
    public RpcContext setRemoteAddress(InetSocketAddress address) {
        this.remoteAddress = address;
        return this;
    }
    
    /**
     * set remote address.
     * 
     * @param host
     * @param port
     * @return context
     */
    public RpcContext setRemoteAddress(String host, int port) {
        if (port < 0) {
            port = 0;
        }
        this.remoteAddress = InetSocketAddress.createUnresolved(host, port);
        return this;
    }

	/**
	 * get remote address.
	 * 
	 * @return remote address
	 */
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	
	/**
	 * get remote address string.
	 * 
	 * @return remote address string.
	 */
	public String getRemoteAddressString() {
	    return getRemoteHost() + ":" + getRemotePort();
	}
	
	/**
	 * get remote host name.
	 * 
	 * @return remote host name
	 */
	public String getRemoteHostName() {
		return remoteAddress == null ? null : remoteAddress.getHostName();
	}

    /**
     * get local host.
     * 
     * @return local host
     */
    public String getLocalHost() {
        String host = localAddress == null ? null : 
            localAddress.getAddress() == null ? localAddress.getHostName() 
                    : NetUtils.filterLocalHost(localAddress.getAddress().getHostAddress());
        if (host == null || host.length() == 0) {
            return NetUtils.getLocalHost();
        }
        return host;
    }

    /**
     * get local port.
     * 
     * @return port
     */
    public int getLocalPort() {
        return localAddress == null ? 0 : localAddress.getPort();
    }

    /**
     * get remote host.
     * 
     * @return remote host
     */
    public String getRemoteHost() {
        return remoteAddress == null ? null : 
            remoteAddress.getAddress() == null ? remoteAddress.getHostName() 
                    : NetUtils.filterLocalHost(remoteAddress.getAddress().getHostAddress());
    }

    /**
     * get remote port.
     * 
     * @return remote port
     */
    public int getRemotePort() {
        return remoteAddress == null ? 0 : remoteAddress.getPort();
    }

    /**
     * get attachment.
     * 
     * @param key
     * @return attachment
     */
    public String getAttachment(String key) {
        return attachments.get(key);
    }

    /**
     * set attachment.
     * 
     * @param key
     * @param value
     * @return context
     */
    public RpcContext setAttachment(String key, String value) {
        if (value == null) {
            attachments.remove(key);
        } else {
            attachments.put(key, value);
        }
        return this;
    }

    /**
     * remove attachment.
     * 
     * @param key
     * @return context
     */
    public RpcContext removeAttachment(String key) {
        attachments.remove(key);
        return this;
    }

    /**
     * get attachments.
     * 
     * @return attachments
     */
    public Map<String, String> getAttachments() {
        return attachments;
    }

    /**
     * set attachments
     * 
     * @param attachment
     * @return context
     */
    public RpcContext setAttachments(Map<String, String> attachment) {
        this.attachments.clear();
        if (attachment != null && attachment.size() > 0) {
            this.attachments.putAll(attachment);
        }
        return this;
    }
    
    public void clearAttachments() {
        this.attachments.clear();
    }

    /**
     * get values.
     * 
     * @return values
     */
    public Map<String, Object> get() {
        return values;
    }

    /**
     * set value.
     * 
     * @param key
     * @param value
     * @return context
     */
    public RpcContext set(String key, Object value) {
        if (value == null) {
            values.remove(key);
        } else {
            values.put(key, value);
        }
        return this;
    }

    /**
     * remove value.
     * 
     * @param key
     * @return value
     */
    public RpcContext remove(String key) {
        values.remove(key);
        return this;
    }

    /**
     * get value.
     * 
     * @param key
     * @return value
     */
    public Object get(String key) {
        return values.get(key);
    }


    /**
     * @deprecated Replace to isProviderSide()
     */
    @Deprecated
    public boolean isServerSide() {
        return isProviderSide();
    }
    
    /**
     * @deprecated Replace to isConsumerSide()
     */
    @Deprecated
    public boolean isClientSide() {
        return isConsumerSide();
    }
    
    /**
     * 异步调用 ，需要返回值，即使步调用Future.get方法，也会处理调用超时问题.
     * @param callable
     * @return 通过future.get()获取返回结果.
     */
    @SuppressWarnings("unchecked")
	public <T> Future<T> asyncCall(Callable<T> callable) {
    	try {
	    	try {
	    		setAttachment(Constants.ASYNC_KEY, Boolean.TRUE.toString());
				final T o = callable.call();
				//local调用会直接返回结果.
				if (o != null) {
					FutureTask<T> f = new FutureTask<T>(new Callable<T>() {
						@Override
                        public T call() throws Exception {
							return o;
						}
					});
					f.run();
					return f;
				} else {
					
				}
			} catch (Exception e) {
				throw new RpcException(e);
			} finally {
				removeAttachment(Constants.ASYNC_KEY);
			}
    	} catch (final RpcException e) {
			return new Future<T>() {
				@Override
                public boolean cancel(boolean mayInterruptIfRunning) {
					return false;
				}
				@Override
                public boolean isCancelled() {
					return false;
				}
				@Override
                public boolean isDone() {
					return true;
				}
				@Override
                public T get() throws InterruptedException, ExecutionException {
					throw new ExecutionException(e.getCause());
				}
				@Override
                public T get(long timeout, TimeUnit unit)
						throws InterruptedException, ExecutionException,
						TimeoutException {
					return get();
				}
			};
		}
    	return ((Future<T>)getContext().getFuture());
    }
    
	/**
	 * oneway调用，只发送请求，不接收返回结果.
	 */
	public void asyncCall(Runnable runable) {
    	try {
    		setAttachment(Constants.RETURN_KEY, Boolean.FALSE.toString());
    		runable.run();
		} catch (Throwable e) {
			throw new RpcException("oneway call error ." + e.getMessage());
		} finally {
			removeAttachment(Constants.RETURN_KEY);
		}
    }

    public String getServiceAddr() {
        return serviceAddr;
    }

    public void setServiceAddr(String serviceAddr) {
        this.serviceAddr = serviceAddr;
    }
}