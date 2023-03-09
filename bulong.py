import time
from typing import Tuple

import bitarray
import mmh3


class BloomFilter:
    def __init__(self, length) -> None:
        self.length = length  # 二进制数组长度
        self.bit_array = bitarray.bitarray(self.length)
        self.bit_array.setall(0)
        self.hash_count = 9  # hash函数个数

    def mightContains(self, key) -> bool:
        out = True
        for i in range(self.hash_count):
            index = BloomFilter.hashKey(self, key, 2 ** (i + 3))
            if self.bit_array[index] == 0:
                out = False

        return out

    def add(self, key) -> None:
        for i in range(self.hash_count):
            index = BloomFilter.hashKey(self, key, 2 ** (i + 3))
            self.bit_array[index] = 1
        return self

    def hashKey(self, key, num) -> Tuple:
        return mmh3.hash(key, num, signed=False) % self.length


T1 = time.time()
length = 1000000
bf = BloomFilter(length)
print("length: ", length)
print("hash_count: ", bf.hash_count)
for i in range(9000):
    bf.add(str(i))

matched = 0
for i in range(9000):
    if bf.mightContains(str(i)):
        matched += 1
print("matched", matched)

missed = 0
for i in range(9000, 90000):
    if not bf.mightContains(str(i)):
        missed += 1
print('missed', missed)
T2 = time.time()
print("Time: ", T2 - T1)
