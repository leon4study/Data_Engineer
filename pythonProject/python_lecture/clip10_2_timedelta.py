import datetime

now = datetime.datetime.now()
past = datetime.datetime(2000,10,22)

delta = now - past
print(delta)
print(type(delta))

print(delta.days, delta.seconds, delta.microseconds)
print(delta.total_seconds())

add_delta = datetime.timedelta(days=90, seconds = 3600)
future = now + add_delta
print(future)