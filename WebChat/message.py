from selenium import webdriver
import unittest
from ddt import ddt,data,unpack
import os,sys,csv
import time


def getCsv(file_name):
    rows=[]
    path=sys.path[0]

    with open(path + '/data/' + file_name, 'rt') as f:
        readers = csv.reader(f, delimiter=',', quotechar='|')
        next(readers, None)
        for row in readers:
            temprows = []
            for i in row:
                temprows.append(i)
            rows.append(temprows)
        return rows
@ddt
class TestCase2(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()
        self.url = "http://localhost:8080/WebChat/"
        self.driver.maximize_window()
        time.sleep(2)

    def tearDown(self):
        self.driver.quit()

    @data(*getCsv('test_message_data.txt'))
    @unpack
    def test_sendMessage(self, value1, value2):
            driver = self.driver
            driver.get(self.url)

            try:
                print(driver.title)
                self.assertEqual(u"在线聊天", driver.title)
            except:
                self.saveScreenShot(driver, "chat.png")

            self.driver.find_element_by_xpath("//*[@id='app']/nav/div[1]/div[1]/div[2]/div[2]").click()
            time.sleep(2)
            self.driver.find_element_by_xpath("//*[@id='input-34']").send_keys(value1)
            self.driver.find_element_by_xpath("//*[@id='input-37']").send_keys(value2)
            self.driver.find_element_by_xpath("//*[@id='app']/div[3]/div/div/div[2]/div/div[3]/div[1]/button/span").click()
            time.sleep(2)
            self.driver.find_element_by_xpath("//*[@id='app']/div[1]/main/div/div[1]/div/div[2]/div[2]/div[2]/div").click()
            time.sleep(2)

            self.driver.find_element_by_xpath("//*[@id='input-23']").send_keys("来了111")
            time.sleep(2)
            self.driver.find_element_by_xpath(
                "//*[@id='app']/div[1]/main/div/div[2]/footer/div/div/div[2]/button/span").click()
            time.sleep(2)

            self.driver.find_element_by_xpath("//*[@id='input-23']").send_keys("来了222")
            time.sleep(2)
            self.driver.find_element_by_xpath(
                "//*[@id='app']/div[1]/main/div/div[2]/footer/div/div/div[2]/button/span").click()
            time.sleep(2)
            self.driver.find_element_by_xpath("//*[@id='app']/nav/div[1]/div[2]/div[2]/div[2]/div").click()

    def saveScreenShot(self, driver, file_name):
        if not os.path.exists("./errorImage"):
            os.mkdir("./errorImage")
        now = time.strftime("%Y%m%d-%H%M%S", time.localtime(time.time()))
        driver.get_screenshot_as_file("./errorImage/" + now + "-" + file_name)
        time.sleep(3)

if __name__ == "__main__":
    unittest.main(verbosity=0)

