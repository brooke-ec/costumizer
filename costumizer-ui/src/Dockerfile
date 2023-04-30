FROM python:3.11-alpine

COPY requirements.txt /tmp/requirements.txt
RUN pip install -r /tmp/requirements.txt
RUN rm -R /tmp/

COPY app /usr/src/app
COPY static /usr/src/app/static

WORKDIR /usr/src/app

EXPOSE 8080
CMD ["gunicorn", "--workers", "3", "--bind", "0.0.0.0:8080", "wsgi:app"]