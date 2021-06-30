from idlelib import browser


from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium import webdriver
import unittest
from ddt import ddt,data,unpack
import sys,csv,os
import time

def getCsv(file_name):
    rows=[]
    path=sys.path[0]

    with open(path + '/data/' + file_name, 'rt',encoding="utf8") as f:
        readers = csv.reader(f, delimiter=',', quotechar='|')
        next(readers, None)
        for row in readers:
            temprows = []
            for i in row:
                temprows.append(i)
            rows.append(temprows)
        return rows

@ddt
class TestCase1(unittest.TestCase):
    def setUp(self):
        self.driver = webdriver.Chrome()
        self.url = "http://localhost:8080/WebChat/"
        self.driver.maximize_window()
        time.sleep(2)

    def tearDown(self):
        self.driver.quit()

    @data(*getCsv('test_register_data.txt'))
    @unpack
    def test_register(self, value1, value2, value3, value4, value5):
        driver = self.driver
        driver.get(self.url)

        try:
            print(driver.title)
            self.assertEqual(u"在线聊天", driver.title)
        except:
            self.saveScreenShot(driver, "chat.png")

        self.driver.find_element_by_xpath("//*[@id='app']/nav/div[1]/div[1]/div[1]/div[2]/div").click()
        time.sleep(2)
        self.driver.find_element_by_xpath("//*[@id='用户名']").send_keys(value1)
        time.sleep(2)
        self.driver.find_element_by_xpath("//*[@id='密码']").send_keys(value2)
        time.sleep(2)
        self.driver.find_element_by_xpath("//*[@id='确认密码']").send_keys(value3)
        time.sleep(2)
        self.driver.find_element_by_xpath("//*[@id='昵称']").send_keys(value4)
        time.sleep(2)
        self.driver.find_element_by_xpath("//*[@id='特长']").send_keys(value5)
        time.sleep(2)
        self.driver.find_element_by_xpath("//*[@id='app']/div[3]/div/div/div[2]/div/div[6]/div[1]/button/span").click()
        time.sleep(2)
        alert = driver.switch_to.alert
        time.sleep(2)
        message = alert.text
        self.assertEqual("注册成功!",message,msg="注册成功!")
        time.sleep(2)
        alert.accept()
        time.sleep(2)

        EC.presence_of_element_located((By.ID, "input-52"))
        # self.a
        # self.assert browser.find_element_by_xpath("//*[text()='复制成功']")

    def saveScreenShot(self, driver, file_name):
        if not os.path.exists("./errorImage"):
            os.mkdir("./errorImage")
        now = time.strftime("%Y%m%d-%H%M%S", time.localtime(time.time()))
        driver.get_screenshot_as_file("./errorImage/" + now + "-" + file_name)
        time.sleep(3)

if __name__ == "__main__":
    unittest.main(verbosity=0)