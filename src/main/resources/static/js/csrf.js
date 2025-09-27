(() => {
    const META_TOKEN  = document.querySelector('meta[name="_csrf"]');
    const META_HEADER = document.querySelector('meta[name="_csrf_header"]');

    const CSRF_TOKEN  = META_TOKEN  ? META_TOKEN.content  : null;
    const CSRF_HEADER = META_HEADER ? META_HEADER.content : null;

    window.secureFetch = (url, options = {}) => {
        const opts    = { method: 'GET', ...options };
        const headers = new Headers(opts.headers || {});
        const method  = (opts.method || 'GET').toUpperCase();

        if (CSRF_TOKEN && CSRF_HEADER && !['GET', 'HEAD', 'OPTIONS'].includes(method)) {
            headers.set(CSRF_HEADER, CSRF_TOKEN);
        }
        return fetch(url, { ...opts, headers });
    };
})();
